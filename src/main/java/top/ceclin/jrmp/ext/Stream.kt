package top.ceclin.jrmp.ext

import org.joor.Reflect
import java.io.ObjectOutputStream

var ObjectOutputStream.replace: Boolean
    get() = Reflect.on(this).get("enableReplace")
    set(value) {
        Reflect.on(this)
            .set("enableReplace", value)
    }
