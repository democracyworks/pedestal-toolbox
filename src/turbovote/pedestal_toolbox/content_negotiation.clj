(ns turbovote.pedestal-toolbox.content-negotiation
  (:require [io.pedestal.impl.interceptor :refer [interceptor]]
            [liberator.conneg :as conneg]
            [turbovote.pedestal-toolbox.response :as response]
            [ring.util.response :as ring-resp]
            [cheshire.core :as json]
            [cheshire.generate :refer [add-encoder encode-map]]
            [clojure.string :as s]
            [schema.utils :as schema]))

(add-encoder schema.utils.ValidationError
             (fn [error json-generator]
               (let [error-explanation (schema/validation-error-explain error)]
                 (encode-map
                  {:message (str (first error-explanation) " "
                                 (ffirst (rest error-explanation)))
                   :value   (second (second error-explanation))}
                  json-generator))))

(def default-media-type-fns
  {"application/edn" pr-str
   "application/json" json/generate-string
   "text/plain" identity})

(defn negotiate-response-content-type
  "Creates an interceptor with an enter fn that negotiates content
  type based on an ordered sequence of acceptable-media-types, adding
  the best choice to the request at the key :media-type.  If no
  acceptable content type is available, replies with a 406 Not
  Acceptable.

  Its leave fn looks for the :media-type key on the request and
  a :content key on the response and encodes it into the body
  according to the media-type-fns map. A set of default
  media-type-fns is provided."
  ([acceptable-media-types]
     (negotiate-response-content-type acceptable-media-types default-media-type-fns))
  ([acceptable-media-types media-type-fns]
     (interceptor
      :enter
      (fn [ctx]
        (let [accept-header (get-in ctx [:request :headers "accept"] "*/*")]
          (if-let [response-content-type (conneg/best-allowed-content-type
                                          accept-header
                                          acceptable-media-types)]
            (assoc-in ctx [:request :media-type] (s/join "/" response-content-type))
            (assoc ctx :response response/not-acceptable))))
      :leave
      (fn [ctx]
        (let [response-content-type (get-in ctx [:request :media-type])
              media-type-fn (get media-type-fns response-content-type identity)
              response (:response ctx)
              body (:body response)
              encoded-body (media-type-fn body)]
          (assoc ctx :response
                 (-> response
                     (ring-resp/content-type response-content-type)
                     (assoc :body encoded-body))))))))
