(ns complecs.sprites
  "Animation : a map of states to a sequence of [delay TextureRegion] pairs.
Spec : A map of the following format
 {:file fname
 :cell-size [x y] ;; Cell size in pixels
 :animations anims ;; map of states to [delay [cell-row cell-cols]]
 }"
  (:use [complecs.files])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion
                                          SpriteBatch)))



(defn load-animation
  "An animation is a map of states to a sequence of [delay TextureRegion]
pairs."
  [{[cx cy] :cell-size
    :keys [fname animations]}]

  (let [sheet (Texture. (get-file-handle fname))
        cells (TextureRegion/split sheet cx cy)]
    (into {}
          (apply concat
                 (for [[st frames] animations]
                   (for [[delay [r c]] frames]
                     [delay (aget cells r c)]))))))
                     