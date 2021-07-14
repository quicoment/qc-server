package com.quicoment.demo.common.error.custom

class FailCreateResourceException : IllegalStateException {
    constructor() : super()
    constructor(message: String) : super(message)
}
