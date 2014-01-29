(ns turbovote.pedestal-toolbox.content-negotiation
  (:require [io.pedestal.service.impl.interceptor :refer [interceptor]]
            [liberator.conneg :as conneg]
            [turbovote.pedestal-toolbox.response :as response]))

(defn negotiate-content-type
  [acceptable-media-types]
  (interceptor
   :enter
   (fn [ctx]
     (let [accept-header (get-in ctx [:request :headers "accept"] "*/*")
           content-type (conneg/best-allowed-content-type
                         accept-header
                         acceptable-media-types)]
       (if content-type
         (assoc-in ctx [:request :media-type] content-type)
         (assoc ctx :response response/not-acceptable))))))
