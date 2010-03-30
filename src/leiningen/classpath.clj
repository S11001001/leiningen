(ns leiningen.classpath
  (:use (clojure.contrib [java-utils :only (file)]
                         [seq-utils :only (flatten)]
                         [str-utils :only (str-join)])
        [leiningen.core :only (read-project)])
  (:import org.apache.tools.ant.types.Path))

(defn find-lib-jars
  "Returns a seq of Files for all the jars in the project's library directory."
  [project]
  (filter #(.endsWith (.getName %) ".jar")
          (file-seq (file (:library-path project)))))

(defn checkout-deps-paths [project]
  (apply concat (for [dep (.listFiles (file (:root project) "checkouts"))]
                  ;; Note that this resets the leiningen.core/project var!
                  (let [proj (read-project (.getAbsolutePath
                                            (file dep "project.clj")))]
                      (for [d [:source-path :compile-path :resources-path]]
                        (proj d))))))

(defn make-path
  "Constructs an ant Path object from Files and strings."
  [& paths]
  (let [ant-path (Path. nil)]
    (doseq [path paths]
      (.addExisting ant-path (Path. nil (str path))))
    ant-path))

(defn get-classpath
  "Answer a list of classpath entries for PROJECT."
  [project]
  (flatten [(:source-path project)
            (:test-path project)
            (:compile-path project)
            (:resources-path project)
            (checkout-deps-paths project)
            (find-lib-jars project)]))

(defn classpath
  "Print out the classpath in which the project operates, within the
  current directory, in a format suitable for the -classpath option."
  [project]
  (println (str-join java.io.File/pathSeparatorChar (get-classpath project))))
