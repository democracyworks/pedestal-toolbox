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

(def unsupported-media-type
  (-> "Unsupported media type"
      ring-resp/response
      (ring-resp/status 415)))

(defn string-bytes [string]
  (alength (.getBytes string "UTF-8")))

(defn string-response [string]
  (let [byte-count (string-bytes string)]
    (-> string
        ring-resp/response
        (ring-resp/header "Content-Length" byte-count))))
