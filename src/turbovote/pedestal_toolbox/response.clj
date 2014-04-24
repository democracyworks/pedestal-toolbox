(ns turbovote.pedestal-toolbox.response
  (:require [ring.util.response :as ring-resp]))

(defn bad-request [error]
  (-> error
      ring-resp/response
      (ring-resp/status 400)))

(def not-acceptable
  (-> "Not acceptable"
      ring-resp/response
      (ring-resp/status 406)))
