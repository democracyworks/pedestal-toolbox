(ns turbovote.pedestal-toolbox.macros)

(defmacro let-or-reply
  "Usage: (let-or-reply ctx [bindings*] exprs*)

   binding => binding-form test response

   Like if-let, but permits multiple bindings and associates the
   response onto the pedestal context if a test fails"
  [ctx bindings & exprs]
  (cond
   (empty? bindings) `(do ~@exprs)
   (zero? (mod (count bindings) 3))
     (let [[binding-form test response] (take 3 bindings)
           rest-bindings (drop 3 bindings)]
       `(if-let [~binding-form ~test]
          (let-or-reply ~ctx ~rest-bindings ~@exprs)
          (assoc ~ctx :response ~response)))
   :else (throw (IllegalArgumentException. "let-or-reply requires the number of forms in the binding vector to be divisible by 3"))))
