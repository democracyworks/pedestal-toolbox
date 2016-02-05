(ns pedestal-toolbox.cors)

(defn domain-matcher-fn [list-of-strings]
  (let [patterns (map re-pattern list-of-strings)]
    (fn [origin]
      (boolean
       (some #(re-matches % origin) patterns)))))
