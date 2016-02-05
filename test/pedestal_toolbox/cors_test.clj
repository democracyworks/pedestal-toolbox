(ns pedestal-toolbox.cors-test
  (:require [pedestal-toolbox.cors :refer :all]
            [clojure.test :refer :all]))

(deftest cors-domain-matcher-fn-test
  (testing "make sure beta.turbovote.org is allowed"
    (let [f (domain-matcher-fn [#"https://beta[.]turbovote[.]org"])]
      (is (true? (f "https://beta.turbovote.org")))
      (is (false? (f "https://ourtime-beta.turbovote.org")))))
  (testing "make sure turbovote.org domains are allowed"
    (let [f (domain-matcher-fn [#"https://(.+[.])?turbovote[.]org"])]
      (is (true? (f "https://beta.turbovote.org")))
      (is (true? (f "https://ourtime-beta.turbovote.org")))
      (is (false? (f "http://google.com")))
      (is (true? (f "https://turbovote.org")))))
  (testing "make sure wildcard domain works"
    (let [f (domain-matcher-fn [#".+"])]
      (is (true? (f "https://beta.turbovote.org")))
      (is (true? (f "https://ourtime-beta.turbovote.org")))
      (is (true? (f "http://localhost:8080")))
      (is (true? (f "http://localdocker:8080")))
      (is (false? (f "")))))
  (testing "make sure turbovote.org domains are allowed"
    (let [f (domain-matcher-fn [#"https://(.+[.])?turbovote[.]org"
                                #"http://vote[.]donaldtrump[.]com"])]
      (is (true? (f "https://beta.turbovote.org")))
      (is (true? (f "https://ourtime-beta.turbovote.org")))
      (is (false? (f "http://google.com")))
      (is (false? (f "https://google.com")))
      (is (true? (f "https://turbovote.org")))
      (is (false? (f "http://turbovote.org")))
      (is (true? (f "http://vote.donaldtrump.com"))))))
