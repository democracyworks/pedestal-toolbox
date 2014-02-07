(ns turbovote.pedestal-toolbox.body-params
  (:require [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.interceptor :refer [defbefore interceptor]]
            [turbovote.pedestal-toolbox.response :as response]
            [schema.core :as s]))

(defbefore body-params
  [ctx]
  (try
    ((:enter (body-params/body-params)) ctx)
    (catch Exception e
      (assoc ctx :response (response/bad-request (.getMessage e))))))

(defn coerce-body-params
  "Takes a map of coercion fns that potentially raise clojure.lang.ExceptionInfo
   if the coercion is not possible with the provided input."
  ([] (coerce-body-params {}))
  ([coercions]
     (interceptor
      :enter
      (fn [ctx]
        (loop [param-keys [:edn-params :json-params]]
          (if (empty? param-keys)
            ctx
            (let [param-key (first param-keys)]
              (if-let [params (get-in ctx [:request param-key])]
                (try (assoc-in ctx [:request :body-params]
                               ((get coercions param-key identity) params))
                     (catch clojure.lang.ExceptionInfo e
                       (assoc ctx :response (response/bad-request (.getMessage e)))))
                (recur (rest param-keys))))))))))

(defn validate-body-params
  "Given a schema, attempt to validate the body-params against
  it. Renders a 400 if the body-params does not match"
  [schema]
  (interceptor
   :enter
   (fn [ctx]
     (try
       (s/validate schema (get-in ctx [:request :body-params]))
       ctx
       (catch clojure.lang.ExceptionInfo e
         (assoc ctx :response (response/bad-request (.getMessage e))))))))
