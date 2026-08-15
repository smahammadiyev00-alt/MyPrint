package uz.myprint.feature.feature.home.model

data class ProjectUiModel(

    val id: String,

    val title: String,

    val lastEdited: String,

    val progress: Float,

    val preview: Int? = null,

    val status: ProjectStatus

)

enum class ProjectStatus {

    NEW,

    DRAFT,

    SAVED

}