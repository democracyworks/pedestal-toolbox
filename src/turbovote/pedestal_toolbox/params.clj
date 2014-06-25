(ns turbovote.pedestal-toolbox.params
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor :refer [defbefore interceptor]]
            [ring.middleware.keyword-params :as keyword-params]
            [turbovote.pedestal-toolbox.response :as response]
            [schema.core :as s]
            [schema.coerce :as coerce]
            [clj-time.coerce :as t]))

(defn coerce-or-identity [f]
  (fn [x]
    (try
      (or (f x) x)
      (catch Exception e x))))

(let [coercions {java.util.UUID (coerce-or-identity #(java.util.UUID/fromString %))
                 java.util.Date (coerce-or-identity t/to-date)}]
  (defn coercion-matcher [schema]
    (or (coercions schema)
        (coerce/json-coercion-matcher schema))))

(defbefore body-params
  [ctx]
  (try
    (let [new-ctx ((:enter (body-params/body-params)) ctx)
          request (:request new-ctx)]
      (assoc-in new-ctx [:request :body-params]
                (or (:edn-params request)
                    (:json-params request)
                    (:form-params request))))
    (catch Exception e
      (assoc ctx :response (response/bad-request (.getMessage e))))))

(defn keywordize-params
  [param-key]
  (interceptor
   :enter
   (fn [ctx]
     (update-in ctx [:request param-key] #'keyword-params/keyify-params))))

(defn validate-params
  [param-key schema]
  (with-meta
    (interceptor
     :enter
     (fn [ctx]
       (try
         (let [validator (coerce/coercer schema coercion-matcher)
               params (validator (get-in ctx [:request param-key]))]
           (if-let [error (schema.utils/error-val params)]
             (assoc ctx :response (response/bad-request error))
             (assoc-in ctx [:request param-key] params)))
         (catch clojure.lang.ExceptionInfo e
           (assoc ctx :response (response/bad-request e))))))
    {:schema schema}))

(def validate-body-params
  "Given a schema, attempt to validate the body-params against
  it. Renders a 400 if the body-params does not match"
  (partial validate-params :body-params))

(def validate-query-params
  "Given a schema, attempt to validate the query-params against
  it. Renders a 400 if the query-params does not match"
  (partial validate-params :query-params))
