package com.novawall.app.data

data class WallpaperItem(
    val id: String,
    val title: String,
    val category: String,
    val accentHex: String
)

object WallpaperCatalog {
    val all = listOf(
        WallpaperItem("aurora", "Aurora Pulse", "Abstract", "#7C4DFF"),
        WallpaperItem("ocean", "Ocean Drift", "Nature", "#00E5FF"),
        WallpaperItem("ember", "Ember Glow", "Warm", "#FF4081"),
        WallpaperItem("nova", "Nova Rings", "Space", "#B388FF"),
        WallpaperItem("mist", "Midnight Mist", "Calm", "#64FFDA"),
        WallpaperItem("orbit", "Soft Orbit", "Minimal", "#82B1FF")
    )

    fun byId(id: String): WallpaperItem? = all.find { it.id == id }
}
