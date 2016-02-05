(ns pedestal-toolbox.cors)

(defn domain-matcher-fn [patterns]
  (fn [origin]
    (boolean
     (and origin
          (some #(re-matches % origin) patterns)))))
