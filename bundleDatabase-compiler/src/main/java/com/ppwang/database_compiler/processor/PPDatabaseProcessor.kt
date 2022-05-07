package com.ppwang.database_compiler.processor

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import com.google.auto.service.AutoService
import com.ppwang.bundledatabase_runtime.PPTableInfo
import com.ppwang.bundledatabase_runtime.constant.PPClazzConstant
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
//                    mMessager?.printMessage(
//                        Diagnostic.Kind.NOTE, "simpleName : $simpleName, " +
//                                "daoName: ${tableInfo.daoClassName}, serviceName: ${tableInfo.serviceClassName}"
//                    )
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

    /**
     * 生成每个 @Entity类对应的 @Dao实现类
     */
    private fun generateInternalDaoFile(tableInfo: PPTableInfo?) {
        tableInfo?.run {
            // 生成对应的 DaoImp 实现类
            val daoAnno = AnnotationSpec.builder(Dao::class.java)
                .build()
            // 声明继承于父类 PPInternalDao<T>
            val daoSpClazz = ParameterizedTypeName.get(
                ClassName.bestGuess(PPClazzConstant.DAO_INTERNAL_SUPER_CLAZZ_QUALIFIED_NAME),
                ClassName.bestGuess(tableInfo.getQualifiedName())
            )
            val daoTypeSpecBuild = TypeSpec.classBuilder(tableInfo.daoClazzSimpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(daoAnno)
                .superclass(daoSpClazz)
            // 执行 Java 文件生成
            try {
                JavaFile.builder(
                    PPClazzConstant.AUTO_GENERATE_DAO_IMPL_PACKAGE_NAME,
                    daoTypeSpecBuild.build()
                ).build().writeTo(mFiler)
            } catch (e: IOException) {
            }

            // 生成对应的 ServiceImp 实现类
            val sercieSpClazz = ParameterizedTypeName.get(
                ClassName.bestGuess(PPClazzConstant.SERVICE_DEFAULT_SUPER_CLAZZ_QUALIFIED_NAME),
                ClassName.bestGuess(tableInfo.getQualifiedName())
            )
            val databaseQualifiedName = PPClazzConstant.DAOBASE_CLAZZ_QUALIFIEDNAME
            val methodCodeSb = StringBuilder()
            methodCodeSb.append("$databaseQualifiedName db = androidx.room.Room.databaseBuilder(\n")
                .append("        com.ppwang.bundle.database.core.PPDatabase.getInstance().getContext(),\n")
                .append("        $databaseQualifiedName.class,\n")
                .append("        \"$mDatabaseName\"\n")
                .append("        ).build();\n")
                .append("        return db.${tableInfo.daoMethodName}();")
            val sercieMethodSpec =
                MethodSpec.methodBuilder("attachIntenalDaoImpl")
                    .addAnnotation(ClassName.bestGuess("java.lang.Override"))
                    .addAnnotation(ClassName.bestGuess("androidx.annotation.Nullable"))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(daoSpClazz)
                    .addCode(methodCodeSb.toString())
                    .build()
            val serviceTypeSpecBuild = TypeSpec.classBuilder(tableInfo.serviceClazzSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(sercieSpClazz)
                .addMethod(sercieMethodSpec)
            // 执行 Java 文件生成
            try {
                JavaFile.builder(
                    PPClazzConstant.AUTO_GENERATE_SERVICE_IMPL_PACKAGE_NAME,
                    serviceTypeSpecBuild.build()
                ).build().writeTo(mFiler)
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
        val size = mTableInfoList.size
        for (i in 0 until size) {
            val tableInfo = mTableInfoList[i]
            entityListSb
                .append("${tableInfo.getQualifiedName()}.class")
            if (i < size - 1) {
                entityListSb.append(",\n")
            }
        }
        if (entityListSb.isEmpty()) {
            return
        }
        // 添加@Database注解
        val entityListString = entityListSb.toString()
        val databaseAnno = AnnotationSpec.builder(Database::class.java)
            .addMember("entities", "{$entityListString}")
            .addMember("exportSchema", "false")
            .addMember("version", mDatabaseVersion)
            .build()
        val typeSpecBuild = TypeSpec.classBuilder(PPClazzConstant.DAOBASE_CLAZZ_SIMPLENAME)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(databaseAnno)
            .superclass(ClassName.bestGuess("androidx.room.RoomDatabase"))
        // 遍历添加各个@Dao内部实现类的抽象方法体
        mTableInfoList.forEach { tableInfo ->
            val methodSpec = MethodSpec.methodBuilder(tableInfo.daoMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ClassName.bestGuess(tableInfo.daoClazzQualifiedName))
                .build()
            typeSpecBuild.addMethod(methodSpec)
        }

        val javaFile =
            JavaFile.builder(PPClazzConstant.AUTO_GENERATE_PACKAGE_NAME, typeSpecBuild.build())
        try {
            javaFile.build().writeTo(mFiler)
        } catch (e: IOException) {
        }
    }


}
