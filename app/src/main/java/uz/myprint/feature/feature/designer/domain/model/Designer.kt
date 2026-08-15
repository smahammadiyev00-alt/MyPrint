package uz.myprint.feature.feature.designer.domain.model

data class Designer(

    val id: String,

    val name: String,

    val avatarRes: Int,

    val specialties: List<String>,

    val rating: Double,

    val reviewCount: Int,

    val completedProjects: Int,

    val verified: Boolean

)