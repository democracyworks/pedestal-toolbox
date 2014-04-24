(ns turbovote.pedestal-toolbox.response
  (:require [ring.util.response :as ring-resp]))

(defn bad-request [error]
  (-> {:errors error}
      ring-resp/response
      (ring-resp/status 400)))

(def not-acceptable
  (-> {:errors "Not acceptable"}
      ring-resp/response
      (ring-resp/status 406)))
