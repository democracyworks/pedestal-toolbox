(ns pedestal-toolbox.response
  (:require [ring.util.response :as ring-resp]))

(defn bad-request [error]
  (-> error
      ring-resp/response
      (ring-resp/status 400)))

(def not-acceptable
  (-> "Not acceptable"
      ring-resp/response
      (ring-resp/status 406)))

(def unsupported-media-type
  (-> "Unsupported media type"
      ring-resp/response
      (ring-resp/status 415)))
