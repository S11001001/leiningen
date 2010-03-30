(ns leiningen.checkout-deps
  (:use [leiningen.core :only [read-project]]
        [leiningen.classpath :only [checkout-deps-paths]]
        [clojure.contrib.java-utils :only [file]]))

(defn checkout-deps [project]
  ;; TODO: look at all deps that have version-control repo information
  ;; in their jar and check them out into the checkouts directory.
  )
