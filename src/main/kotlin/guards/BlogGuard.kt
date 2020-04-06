package dev.alpas.alpasdev.guards

import dev.alpas.validation.*

class BlogGuard : ValidationGuard() {
    override fun rules(): Map<String, Iterable<Rule>> {

          return mapOf(
              "title" to listOf(Required()),
              "url" to listOf(Required()),
              "body" to listOf(Required())
          )
    }
}

