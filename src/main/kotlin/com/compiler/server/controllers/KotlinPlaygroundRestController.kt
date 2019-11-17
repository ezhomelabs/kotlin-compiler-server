package com.compiler.server.controllers

import com.compiler.server.compiler.components.KotlinProjectExecutor
import com.compiler.server.model.Project
import com.compiler.server.model.ProjectType
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class KotlinPlaygroundRestController(private val kotlinProjectExecutor: KotlinProjectExecutor){

  /**
   * Endpoint for support requests from kotlin playground client.
   * Kotlin Playground component see: https://github.com/JetBrains/kotlin-playground
   * Old server see: https://github.com/JetBrains/kotlin-web-demo
   */
  @PostMapping(
    value = ["/kotlinServer"],
    consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  @Suppress("IMPLICIT_CAST_TO_ANY")
  fun tryKotlinLangObsoleteEndpoint(
    @RequestParam type: String,
    @RequestParam(required = false) line: Int?,
    @RequestParam(required = false) ch: Int?,
    @RequestParam runConf: ProjectType,
    @RequestParam project: Project
  ): ResponseEntity<*> {
    val result = when (type) {
      "run" -> {
        when (runConf) {
          ProjectType.JAVA -> kotlinProjectExecutor.run(project)
          ProjectType.JS, ProjectType.CANVAS -> kotlinProjectExecutor.convertToJs(project)
          else -> error("Unknown 'runCong' $runConf")
        }
      }
      "highlight" -> kotlinProjectExecutor.highlight(project)
      "complete" -> {
        if (line != null && ch != null) {
          kotlinProjectExecutor.complete(project, line, ch)
        }
        else throw error("No parameters 'line' or 'ch'")
      }
      else -> throw error("No parameter 'type' found")
    }
    return ResponseEntity.ok(result)
  }
}