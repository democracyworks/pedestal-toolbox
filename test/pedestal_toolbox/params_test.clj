(ns pedestal-toolbox.params-test
  (:require [clojure.test :refer :all]
            [io.pedestal.http.body-params :as body-params]
            [pedestal-toolbox.params :refer :all]
            [pedestal-toolbox.response :refer [bad-request]]))

(deftest body-params-test
  (let [make-request (fn [body]
                       {:request {:content-type "application/edn"
                                  :body (java.io.ByteArrayInputStream.
                                         (.getBytes body))}})
        good-body "{:a 1}"
        bad-body  "{:a  }"]
    (testing "Acts just like body-params/body-params on good inputs"
      (is (= (read-string good-body)
             (-> good-body
                 make-request
                 ((:enter (body-params/body-params)))
                 (get-in [:request :edn-params]))
             (-> good-body
                 make-request
                 ((:enter (body-params)))
                 (get-in [:request :edn-params])))))
    (testing "Doesn't 415 if charset is set"
      (is (not= 415
                (-> good-body
                    make-request
                    (assoc-in [:request :content-type] "application/edn; charset=UTF-8")
                    ((:enter (body-params)))
                    (get-in [:response :status])))))
    (testing "Returns a 400 (not a 500!) on a bad request"
      (is (= 400 (-> bad-body
                     make-request
                     ((:enter (body-params)))
                     (get-in [:response :status])))))
    (testing "Returns error message on a bad request"
      (is (= "Map literal must contain an even number of forms"
             (-> bad-body
                 make-request
                 ((:enter (body-params)))
                 (get-in [:response :body])))))
    (testing "Copies params to body-params"
      (is (= (read-string good-body)
             (-> good-body
                 make-request
                 ((:enter (body-params)))
                 (get-in [:request :body-params])))))
    (testing "Returns a 415 on an unacceptable content-type"
      (is (= 415 (-> "{}"
                     make-request
                     ((:enter (body-params {#"^application/json" body-params/json-parser})))
                     (get-in [:response :status])))))))

(deftest keywordize-params-test
  (let [params {"a" 1 "b" {"c" 2}}
        ctx {:request {:my-params params}}
        enter (:enter (keywordize-params :my-params))]
    (testing "keywordizes keys"
      (is (= {:request {:my-params {:a 1 :b {:c 2}}}}
             (enter ctx)))
      (is (= {:request {:my-params nil}}
             (enter {:request {}}))))))

(deftest validate-body-params-test
  (let [params {:a 1 :b "abc"}
        ctx {:request {:body-params params}}
        matching-schema {:a Long :b String}
        mismatched-schema {:a String :c String}]
    (testing "with a body that matches the schema"
      (let [enter (:enter (validate-body-params matching-schema))]
        (is (= ctx (enter ctx)))))
    (testing "with a body that does not match the schema"
      (let [enter (:enter (validate-body-params mismatched-schema))]
        (is (= 400 (-> ctx
                       enter
                       :response
                       :status))))))
  (let [schema {:date java.util.Date
                :uuid java.util.UUID}
        enter (:enter (validate-body-params schema))]
    (testing "without needing coercion"
      (let [ctx {:request {:body-params {:date (java.util.Date.)
                                         :uuid (java.util.UUID/randomUUID)}}}]
        (is (= ctx (enter ctx)))))
    (testing "with successful coercion"
      (let [date #inst "1977-08-27"
            uuid (java.util.UUID/randomUUID)
            ctx {:request {:body-params {:date "1977-08-27"
                                         :uuid (str uuid)}}}]
        (is (= {:request {:body-params {:date date :uuid uuid}}}
               (enter ctx)))))
    (testing "with unsuccessful coercion"
      (is (= 400
             (-> {:request {:body-params {:date "4 score and 20 years ago"
                                          :uuid "this is not a UUID!"}}}
                 enter
                 (get-in [:response :status])))))
    (testing "returns error map on validation errors"
      (is (= #{:uuid :date}
             (set (keys (-> {:request
                             {:body-params
                              {:date "4 score and 20 years ago"}}}
                            enter
                            (get-in [:response :body])))))))))

(deftest query-param-accept-test
  (let [params {"a" 1 :accept "application/csv"}
        ctx {:request {:params params}}
        enter (:enter query-param-accept)
        ctx-with-updated-headers (enter ctx)]
    (testing "a query param of content type is placed in the headers"
      (is (= (get-in ctx [:request :params :accept])
             (get-in ctx-with-updated-headers [:request :headers "accept"]))))
    (testing "the original params aren't mutated"
      (is (= (get-in ctx [:request :params "a"]) 1)))))
