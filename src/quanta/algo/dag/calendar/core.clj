(ns quanta.algo.dag.calendar.core
  (:require
   [missionary.core :as m]
   [ta.calendar.core :refer [current-close]]
   [quanta.algo.dag.calendar.live :as live]))

(defn calculate-calendar [dt]
  (fn [cal]
    (let [[market-kw interval-kw] cal
          current-close (current-close market-kw interval-kw dt)]
      (m/seed [current-close]))))

(defn live-calendar [cal]
  (live/get-calendar-flow cal))
