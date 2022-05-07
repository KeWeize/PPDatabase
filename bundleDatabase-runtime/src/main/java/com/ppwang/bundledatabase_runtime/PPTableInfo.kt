package com.ppwang.bundledatabase_runtime

import com.ppwang.bundledatabase_runtime.constant.PPClazzConstant

/***
 * @author: vitar5
 * @time: 2022/5/3
 */
class PPTableInfo constructor(
    private val qualifiedName: String? = null,
    private val simpleName: String
) {

    /**
     * 对应 @Dao 实现类类名
     * eg: PPStudentDefaultDaoImpl
     */
    val daoClazzSimpleName = String.format("PP${simpleName}DefaultDaoImpl")

    /**
     * 对应 @Dao 实现类完整类名
     * eg: com.ppwang.bundle.database.PPStudentDefaultDaoImpl
     */
    val daoClazzQualifiedName =
        String.format("${PPClazzConstant.AUTO_GENERATE_DAO_IMPL_PACKAGE_NAME}.PP${simpleName}DefaultDaoImpl")

    /**
     * 数据库类中对应的获取 @Dao 实例的方法名
     * eg: getPPStudentDefaultDaoImpl
     */
    val daoMethodName = String.format("getPP${simpleName}DefaultDaoImpl")

    /**
     * 对应 Service 实现类类名
     * eg: PPStudentDefaultServiceImpl
     */
    val serviceClazzSimpleName = String.format("PP${simpleName}DefaultServiceImpl")

    /**
     * 对应 Service 实现类完整类名
     * eg: com.ppwang.bundle.database.service.impl.PPStudentDeDefaultServiceImpl
     */
    val serviceClazzQualifiedName =
        String.format("${PPClazzConstant.AUTO_GENERATE_SERVICE_IMPL_PACKAGE_NAME}.PP${simpleName}DefaultServiceImpl")

    /**
     * 获取对应 @Entity对应完整类名
     */
    fun getQualifiedName(): String? = qualifiedName

}