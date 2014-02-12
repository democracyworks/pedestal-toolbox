(ns turbovote.pedestal-toolbox.params
  (:require [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.interceptor :refer [defbefore interceptor]]
            [turbovote.pedestal-toolbox.response :as response]
            [schema.core :as s]))

(defbefore body-params
  [ctx]
  (try
    ((:enter (body-params/body-params)) ctx)
    (catch Exception e
      (assoc ctx :response (response/bad-request (.getMessage e))))))

(defn coerce-body-params
  "Takes a map of coercion fns that potentially raise clojure.lang.ExceptionInfo
   if the coercion is not possible with the provided input."
  ([] (coerce-body-params {}))
  ([coercions]
     (interceptor
      :enter
      (fn [ctx]
        (loop [param-keys [:edn-params :json-params]]
          (if (empty? param-keys)
            ctx
            (let [param-key (first param-keys)]
              (if-let [params (get-in ctx [:request param-key])]
                (try (assoc-in ctx [:request :body-params]
                               ((get coercions param-key identity) params))
                     (catch clojure.lang.ExceptionInfo e
                       (assoc ctx :response (response/bad-request (.getMessage e)))))
                (recur (rest param-keys))))))))))

(defn validate-params
  [param-key schema]
  (with-meta
    (interceptor
     :enter
     (fn [ctx]
       (try
         (s/validate schema (get-in ctx [:request param-key]))
         ctx
         (catch clojure.lang.ExceptionInfo e
           (assoc ctx :response (response/bad-request (.getMessage e)))))))
    {:schema schema}))

(def validate-body-params
  "Given a schema, attempt to validate the body-params against
  it. Renders a 400 if the body-params does not match"
  (partial validate-params :body-params))

(def validate-query-params
  "Given a schema, attempt to validate the query-params against
  it. Renders a 400 if the query-params does not match"
  (partial validate-params :query-params))
