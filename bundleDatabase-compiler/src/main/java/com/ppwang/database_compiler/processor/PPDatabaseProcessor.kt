package com.ppwang.database_compiler.processor

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import com.google.auto.service.AutoService
import com.ppwang.bundledatabase_runtime.PPTableInfo
import com.squareup.javapoet.*
import java.io.IOException
import java.lang.StringBuilder
import java.util.LinkedHashSet
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
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

    private var mFiler: Filer? = null

    /**
     * 缓存@Entity修饰类生成对应的表信息
     */
    private val mTableInfoList = ArrayList<PPTableInfo>()

    /**
     * 获取动态配置数据库名称、版本号
     */
    private var mDatabaseName = "pp_database"
    private var mDatabaseVersion = "1"

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        // 初始化操作
        mMessager = processingEnv?.messager
        mFiler = processingEnv?.filer
        // 获取数据库信息
        val options = processingEnv?.options
        options?.run {
            if (containsKey("databaseVersion")) {
                mDatabaseVersion = get("databaseVersion") ?: mDatabaseVersion
            }
            if (containsKey("databaseName")) {
                mDatabaseName = get("databaseName") ?: mDatabaseName
            }
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
            // 生成内部默认@Dao文件
            makeSuprePPInternalDao()
            // 生成数据库文件
            makeSurePPDatabase()
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
                    // 类名
                    val qualifiedName = entityElement.qualifiedName.toString()
                    val simpleName = entityElement.simpleName.toString()
                    val tableInfo = PPTableInfo(qualifiedName, simpleName)
                    mTableInfoList.add(tableInfo)
                    mMessager?.printMessage(
                        Diagnostic.Kind.NOTE, "simpleName : $simpleName, " +
                                "daoName: ${tableInfo.daoClassName}, serviceName: ${tableInfo.serviceClassName}"
                    )
                }
            } catch (e: Exception) {
                mMessager?.printMessage(Diagnostic.Kind.ERROR, e.message)
            }
        }
    }

    /**
     * 确保生成每个@Entity实体有一个对应的默认@Dao文件
     */
    private fun makeSuprePPInternalDao() {
        if (mTableInfoList.isEmpty()) {
            return
        }
        mTableInfoList.forEach { tableInfo ->
            generateInternalDaoFile(tableInfo)
        }
    }

    private fun generateInternalDaoFile(tableInfo: PPTableInfo?) {
        tableInfo?.run {
            // 添加 @DAO 注解
            val daoAnno = AnnotationSpec.builder(Dao::class.java)
                .build()
            val typeSpecBuild = TypeSpec.classBuilder(tableInfo.daoClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(daoAnno)
                .superclass(ClassName.bestGuess("com.ppwang.bundle.database.dao.PPInternalDao"))
            // 生成动态java文件
            val packageName = String.format(
                "%s.%s",
                PPTableInfo.AUTO_GENERATE_PACKAGE_NAME, PPTableInfo.DATABASE_DAO_IMPL_PATH
            )
            val javaFile =
                JavaFile.builder(packageName, typeSpecBuild.build())
            try {
                javaFile.build().writeTo(mFiler)
            } catch (e: IOException) {
            }
        }
    }

    /**
     * 生成Room数据库类型
     */
    private fun makeSurePPDatabase() {
        if (mTableInfoList.isEmpty()) {
            return
        }
        val entityListSb = StringBuilder()
        mTableInfoList.forEach { tableInfo ->
            tableInfo.getQualifiedName()?.let { qualifiedName ->
                entityListSb.append("${qualifiedName}.class, ")
            }
        }
        if (entityListSb.length < 2) {
            return
        }
        val entityListString = entityListSb.subSequence(0, entityListSb.length - 2).toString()
        val databaseAnno = AnnotationSpec.builder(Database::class.java)
            .addMember("entities", "{$entityListString}")
            .addMember("exportSchema", "false")
            .addMember("version", mDatabaseVersion)
            .build()
        val typeSpecBuild = TypeSpec.classBuilder("PPRoomDatabase")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(databaseAnno)
            .superclass(ClassName.bestGuess("androidx.room.RoomDatabase"))

        // 遍历添加各个@Dao内部实现类的抽象方法体
//        mTableInfoList.forEach { tableInfo ->
            val daoImplPackageName = String.format(
                "%s.%s",
                PPTableInfo.AUTO_GENERATE_PACKAGE_NAME, PPTableInfo.DATABASE_DAO_IMPL_PATH
            )
            val methodSpec = MethodSpec.methodBuilder("getPPVipEntityDefaultDaoImpl")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ClassName.bestGuess("com.ppwang.bundle.database.dao.impl.PPVipEntityDefaultDaoImpl"))
                .build()
//            typeSpecBuild.addMethod(methodSpec)
//        }

        val javaFile =
            JavaFile.builder(PPTableInfo.AUTO_GENERATE_PACKAGE_NAME, typeSpecBuild.build())
        try {
            javaFile.build().writeTo(mFiler)
        } catch (e: IOException) {
        }
    }


}
