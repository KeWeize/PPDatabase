package com.ppwang.bundledatabase_runtime

/***
 * @author: vitar5
 * @time: 2022/5/3
 */
class PPTableInfo constructor(
    private val qualifiedName: String? = null,
    private val simpleName: String
) {

    companion object {

        /**
         * 自动生成的 dao、service实现类包名
         */
        const val AUTO_GENERATE_PACKAGE_NAME = "com.ppwang.bundle.database"

        /**
         * 自动生成的 dao实现类包路径
         */
        const val DATABASE_DAO_IMPL_PATH = "dao.impl"

        /**
         * 自动生成的 service实现类包路径
         */
        const val DATABASE_SERVICE_IMPL_PATH = "service.impl"

    }

    val daoClassName: String
        get() {
            return String.format("PP${simpleName}DefaultDaoImpl")
        }

    val serviceClassName: String
        get() {
            return String.format("PP${simpleName}ServiceImpl")
        }

    /**
     * 获取对应 @Entity对应完整类名
     */
    fun getQualifiedName(): String? = qualifiedName

}