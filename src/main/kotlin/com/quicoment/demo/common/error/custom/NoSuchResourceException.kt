package com.quicoment.demo.common.error.custom

class NoSuchResourceException : NoSuchElementException {
    constructor() : super()
    constructor(message: String) : super(message)
}
