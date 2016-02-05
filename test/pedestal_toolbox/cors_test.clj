(ns pedestal-toolbox.cors-test
  (:require [pedestal-toolbox.cors :refer :all]
            [clojure.test :refer :all]))

(deftest cors-domain-matcher-fn-test
  (testing "make sure beta.turbovote.org is allowed"
    (let [f (domain-matcher-fn ["beta[.]turbovote[.]org"])]
      (is (true? (f "beta.turbovote.org")))))
  (testing "make sure ourtime-beta.turbovote.org is allowed"
    (let [f (domain-matcher-fn [".*[.]turbovote[.]org"])]
      (is (true? (f "beta.turbovote.org")))
      (is (true? (f "ourtime-beta.turbovote.org")))))
  (testing "make sure ourtime-beta.turbovote.org is allowed"
    (let [f (domain-matcher-fn [".*"])]
      (is (true? (f "beta.turbovote.org")))
      (is (true? (f "ourtime-beta.turbovote.org"))))))
