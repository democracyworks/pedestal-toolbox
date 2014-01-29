(ns turbovote.pedestal-toolbox.body-params
  (:require [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.interceptor :refer [defbefore]]
            [turbovote.pedestal-toolbox.response :as response]))

(defbefore body-params
  [ctx]
  (try
    ((:enter (body-params/body-params)) ctx)
    (catch Exception e
      (assoc ctx :response (response/bad-request (.getMessage e))))))
