package com.dgioto.drawingforchildren

import android.R.attr
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.dgioto.drawingforchildren.ui.BottomPanel
import com.dgioto.drawingforchildren.ui.theme.DrawingForChildrenTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pathData = remember {
                mutableStateOf(PathData())
            }
            val pathList = remember {
                mutableStateListOf(PathData())
            }

            DrawingForChildrenTheme {
                Column {
                    BottomPanel(
                        { color ->
                            //обновляет значение объекта pathData, заменяя его на копию
                            // с обновленным полем color
                            pathData.value = pathData.value.copy(color = color)
                        },
                        { lineWidth ->
                            pathData.value = pathData.value.copy(lineWidth = lineWidth)
                        })
                    {
                        pathList.removeIf{ pathD ->
                            pathList[pathList.size - 1] == pathD
                        }
                    }
                    DrawCanvas(pathData, pathList)
                }
            }
        }
    }
}

@Composable
fun DrawCanvas(pathData: MutableState<PathData>, pathList: SnapshotStateList<PathData>) {
    var tempPath = Path()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pointerInput(true) {
                //Определяет обработчики жестов перетаскивания на холсте. onDragStart вызывается
                // при начале перетаскивания, а onDragEnd - при завершении
                detectDragGestures(
                    onDragStart = {
                        tempPath = Path()
                    },
                    onDragEnd = {
                        pathList.add(
                            pathData.value.copy(
                                path = tempPath
                            )
                        )
                    }
                ) { change, dragAmount ->
                    //Обновляют временный путь tempPath при перемещении пальца пользователя по экрану
                    tempPath.moveTo(
                        change.position.x - dragAmount.x,
                        change.position.y - dragAmount.y
                    )
                    tempPath.lineTo(
                        change.position.x,
                        change.position.y
                    )
                    //Проверяют, есть ли элементы в списке pathList, и, если есть,
                    // удаляют последний элемент
                    if (pathList.size > 0) {
                        pathList.removeAt(pathList.size - 1)
                    }
                    //Добавляет обновленный путь в список pathList
                    pathList.add(
                        pathData.value.copy(
                            path = tempPath
                        )
                    )
                }
            }
    ) {
        //Итерируется по элементам списка pathList
        pathList.forEach { pathData ->
            //Рисует путь на холсте с указанным цветом и стилем
            drawPath(
                pathData.path,
                color = pathData.color,
                style = Stroke(
                    pathData.lineWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}