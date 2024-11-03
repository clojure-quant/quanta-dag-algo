(ns dev.algo-simple-template
  (:require
   [tick.core :as t]
   [quanta.algo.template :as templ]
   [dev.algo-simple :refer [simple-algo]]))

(defn viz-print [opts data]
  {:creator "viz-print"
   :data data
   :viz-opts opts})

(def simple-template
  {:id :simple
   :algo simple-algo
   :options [{:type :select
              :path [:* :x]
              :name "x param"
              :spec [200 500 1000 2000]}
             {:type :string
              :path [:algo :z]
              :name "z param (with coercion)"
              :coerce :double}]
   :print {:viz viz-print
           :key :algo
           :viz-options {:print-mode :simple}}})

; this is called from the web-ui upon selecting a template
(templ/template-info simple-template)
;; => {:options
;;     [{:type :select, :path [:* :x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:algo :z], :name "z param (with coercion)", :coerce :double}],
;;     :current {[:* :x] 3, [:algo :z] nil},
;;     :views [:select-viz :print]}

(templ/apply-options simple-template {[:* :x] 18})
;; => {:id :simple,
;;     :algo {:* {:x 18}, :algo {:calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :y :b, :z nil}},
;;     :options
;;     [{:type :select, :path [:* :x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:algo :z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

; coercion not enabled
(templ/apply-options simple-template {[:algo :z] "15.333"})
;; => {:id :simple,
;;     :algo {:* {:x 3}, :algo {:calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :y :b, :z "15.333"}},
;;     :options
;;     [{:type :select, :path [:* :x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:algo :z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

(templ/apply-options simple-template {[:algo :z] "15.333"} true)
;; => {:id :simple,
;;     :algo {:* {:x 3}, :algo {:calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :y :b, :z 15.333}},
;;     :options
;;     [{:type :select, :path [:* :x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:algo :z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

(templ/apply-options simple-template {[:algo :z] "15.333"
                                      [:* :x] 27} true)
;; => {:id :simple,
;;     :algo {:* {:x 27}, :algo {:calendar [:crypto :m], :fn #function[dev.algo-simple/simple-calc], :y :b, :z 15.333}},
;;     :options
;;     [{:type :select, :path [:* :x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:algo :z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

(templ/calculate
 {:log-dir ".data/"
  :env {}}
 simple-template
 :print
 (t/instant))
;; => {:creator "viz-print",
;;     :data {:result #time/zoned-date-time "2024-11-03T19:03Z[UTC]", :opts {:x 3, :y :b, :z nil, :calendar [:crypto :m]}},
;;     :viz-opts {:print-mode :simple}}




