
(ns complecs.files
  "Provides an interface to libGDX's file management system."
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx.files FileHandle
                                   )))


(def absolute ::absolute)
(def internal ::internal)
(def classpath ::classpath)
(def local ::local)

(def ^:dynamic *file-mode*
  "Determines file loading mode,
either absolute, internal, classpath, or local."
  absolute)

(defn get-file-handle
  "Get a file handle based on the current file mode."
  [fname]
  (case *file-mode*
    absolute (.absolute Gdx/files fname)
    internal (.internal Gdx/files fname)
    classpath (.classpath Gdx/files fname)
    local (.local Gdx/files fname)))