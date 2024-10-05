(ns dev.algo-simple-template
  (:require
   [tick.core :as t]
   [quanta.algo.template :as templ]
   [quanta.dag.env :refer [log]]
   [dev.algo-simple :refer [simple-algo]]))

(defn viz-print [opts data]
  (log "calculating viz-fn with data: " data)
  {:creator "viz-print"
   :data data
   :viz-opts opts})

(def simple-template
  {:id :simple
   :algo simple-algo
   :options [{:type :select
              :path [:x]
              :name "x param"
              :spec [200 500 1000 2000]}
             {:type :string
              :path [:z]
              :name "z param (with coercion)"
              :coerce :double}]
   :print {:viz viz-print
           :viz-options {:print-mode :simple}}})

; this is called from the web-ui upon selecting a template
(templ/template-info simple-template)

(templ/apply-options simple-template {[:x] 18})
;; => {:id :simple,
;;     :algo {:calendar [:crypto :m], :algo #function[dev.algo-simple/simple-calc], :x 18, :y :b, :z nil},
;;     :options
;;     [{:type :select, :path [:x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

; coercion not enabled
(templ/apply-options simple-template {[:z] "15.333"})
;; => {:id :simple,
;;     :algo {:calendar [:crypto :m], :algo #function[dev.algo-simple/simple-calc], :x 3, :y :b, :z "15.333"},
;;     :options
;;     [{:type :select, :path [:x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

(templ/apply-options simple-template {[:z] "15.333"} true)
;; => {:id :simple,
;;     :algo {:calendar [:crypto :m], :algo #function[dev.algo-simple/simple-calc], :x 3, :y :b, :z 15.333},
;;     :options
;;     [{:type :select, :path [:x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

(templ/apply-options simple-template {[:z] "15.333"
                                      [:x] 27} true)
;; => {:id :simple,
;;     :algo {:calendar [:crypto :m], :algo #function[dev.algo-simple/simple-calc], :x 27, :y :b, :z 15.333},
;;     :options
;;     [{:type :select, :path [:x], :name "x param", :spec [200 500 1000 2000]}
;;      {:type :string, :path [:z], :name "z param (with coercion)", :coerce :double}],
;;     :print {:viz #function[dev.algo-simple-template/viz-print], :viz-options {:print-mode :simple}}}

(templ/calculate
 {:log-dir ".data/"
  :env {}}
 simple-template
 :print
 (t/instant))
;; => {:creator "viz-print",
;;     :data {:result #time/zoned-date-time "2024-10-03T00:21Z[UTC]", :opts {:calendar [:crypto :m], :x 3, :y :b, :z nil}},
;;     :viz-opts {:print-mode :simple}}



