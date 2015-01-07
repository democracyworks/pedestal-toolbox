(ns turbovote.pedestal-toolbox.response-test
  (:require [turbovote.pedestal-toolbox.response :refer :all]
            [clojure.test :refer :all]))

(deftest string-response-test
  (testing "sets Content-Length header"
    (is (= "5" (get-in (string-response "fooñ") [:headers "Content-Length"])))))
