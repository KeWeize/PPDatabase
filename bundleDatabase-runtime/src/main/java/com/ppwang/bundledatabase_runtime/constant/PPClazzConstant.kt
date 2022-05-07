package com.ppwang.bundledatabase_runtime.constant

/***
 * @author: vitar5
 * @time: 2022/5/7
 */
object PPClazzConstant {

    /**
     * @Dao 基类完整类名
     */
    const val DAO_INTERNAL_SUPER_CLAZZ_QUALIFIED_NAME =
        "com.ppwang.bundle.database.dao.PPInternalDao"

    /**
     * Service 基类完整类名
     */
    const val SERVICE_DEFAULT_SUPER_CLAZZ_QUALIFIED_NAME =
        "com.ppwang.bundle.database.service.PPDefaultService"

    /**
     * 编译时生成类的包名
     */
    const val AUTO_GENERATE_PACKAGE_NAME = "com.ppwang.bundle.database"

    /**
     * 生成的 @Dao 实现类包名
     */
    const val AUTO_GENERATE_DAO_IMPL_PACKAGE_NAME = "$AUTO_GENERATE_PACKAGE_NAME.dao.impl"

    /**
     * 生成的 service实现类包名
     */
    const val AUTO_GENERATE_SERVICE_IMPL_PACKAGE_NAME = "$AUTO_GENERATE_PACKAGE_NAME.service.impl"

    /**
     * Room 数据库类类名
     */
    const val DAOBASE_CLAZZ_SIMPLENAME = "PPRoomDatabase"

    /**
     * Room 数据库类完整类名
     */
    const val DAOBASE_CLAZZ_QUALIFIEDNAME = "$AUTO_GENERATE_PACKAGE_NAME.$DAOBASE_CLAZZ_SIMPLENAME"

}