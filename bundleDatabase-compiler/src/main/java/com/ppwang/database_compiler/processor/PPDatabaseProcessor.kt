package com.ppwang.database_compiler.processor

import androidx.room.Entity
import com.google.auto.service.AutoService
import java.util.LinkedHashSet
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * @author: vitar5
 * @time: 2022/4/30
 */
@AutoService(Processor::class)
class PPDatabaseProcessor : AbstractProcessor() {

    private companion object {
        /**
         * 需要进行处理的目标注解类型
         */
        private val SUPPORTED_ANNOTATION_TYPES = listOf(
            Entity::class.java,
        )
    }

    /**
     * 消息输出对象
     */
    private var mMessager: Messager? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        // 初始化操作
        mMessager = processingEnv?.messager
        // 获取数据库信息
        val options = processingEnv?.options
        options?.run {
            val databaseVersion = getOrDefault("databaseVersion", 1)
            val databaseName = if (containsKey("databaseName")) {
                get("databaseName")
            } else {
                "p_database"
            }

            mMessager?.printMessage(Diagnostic.Kind.NOTE, "version: $databaseVersion, name: $databaseName")
        }

    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val types: LinkedHashSet<String> =
            LinkedHashSet()
        SUPPORTED_ANNOTATION_TYPES.forEach { annotationClazz ->
            types.add(annotationClazz.canonicalName)
        }
        return types
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.run {
            // 遍历并缓存所有目标注解类型对象
            findAndParseTargets(roundEnv)
        }
        return false
    }

    /**
     * 遍历并解析目标注解
     */
    private fun findAndParseTargets(env: RoundEnvironment) {
        env.getElementsAnnotatedWith(Entity::class.java).forEach { entityElement ->
            try {
                if (entityElement.kind == ElementKind.CLASS && entityElement is TypeElement) {
                    // 完整类名
                    val qualifiedName = entityElement.qualifiedName.toString()
                    // 类名
                    val simpleName = entityElement.simpleName.toString()
                    // 包名
                    val packName =
                        qualifiedName.substring(0, qualifiedName.length - simpleName.length - 1)
                    mMessager?.printMessage(Diagnostic.Kind.NOTE, "qualifiedName : $qualifiedName")
                }
            } catch (e: Exception) {
                mMessager?.printMessage(Diagnostic.Kind.ERROR, e.message)
            }
        }
    }

}
