package de.drick.compose.hotpreview.plugin

import com.intellij.openapi.vfs.VirtualFile
import de.drick.compose.hotpreview.HotPreview
import org.jetbrains.kotlin.load.kotlin.PackagePartClassUtils
import org.jetbrains.kotlin.name.FqName

val fqNameHotPreview = requireNotNull(HotPreview::class.qualifiedName)

data class ComposableFunctionInfo(
    val name: String,
    val sourceFileName: String,
    val className: String,
    val lineNumber: Int
)

data class HotPreviewFunction(
    val name: String,
    val annotation: List<HotPreview>
)

fun kotlinFileHasHotPreview(kotlinFile: VirtualFile): Boolean = kotlinFile.inputStream
    .bufferedReader()
    .useLines { lines ->
        lines.any { it.contains(fqNameHotPreview) }
    }

private val packageMatcher = Regex("""package\s+([a-z][a-z0-9_]*(\.[a-z0-9_]+)*[a-z0-9_]*)""")
fun kotlinFileClassName(kotlinFile: VirtualFile): String {
    //TODO use PsiFile to analyze the file
    //TODO support also the @JvmName annotation
    val packageName = kotlinFile.inputStream.bufferedReader().useLines { lines ->
        lines.find { it.trimStart().startsWith("package") }?.let { packageLine ->
            packageMatcher
                .find(packageLine)
                ?.groupValues
                ?.getOrNull(1)
        }
    } ?: ""
    val className = PackagePartClassUtils.getPackagePartFqName(FqName(packageName), kotlinFile.name)
        .toString()
    return className
}