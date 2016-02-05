(ns pedestal-toolbox.cors)

(defn domain-matcher-fn [patterns]
  (fn [origin]
    (boolean
     (some #(re-matches % origin) patterns))))
