package com.dgioto.drawingforchildren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.dgioto.drawingforchildren.ui.theme.DrawingForChildrenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawingForChildrenTheme {
                DrawCanvas()
            }
        }
    }
}

@Composable
fun DrawCanvas() {
    //Этот объект будет использоваться для временного хранения пути, который рисует пользователь
    // во время перемещения пальца по экрану.
    val tempPath = Path()
    //Она представляет собой изменяемое состояние (mutableState), которое хранит текущий путь,
    // нарисованный пользователем.
    val path = remember {
        mutableStateOf(Path())
    }

    //Canvas - это компонент Jetpack Compose, предназначенный для рисования на экране.
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            //Этот блок настраивает обработку событий указателя (событий, связанных с касаниями и
            // движениями пальцев на экране).
            .pointerInput(true) {
                //Этот блок кода реагирует на жесты перетаскивания (drag) на холсте. change
                // представляет информацию о событии, а dragAmount представляет величину смещения,
                // связанную с перемещением пальца.
                detectDragGestures { change, dragAmount ->
                    //Здесь обновляется временный путь tempPath, добавляя точки, которые
                    // пользователь двигает по экрану, чтобы нарисовать линию на холсте.
                    tempPath.moveTo(
                        change.position.x - dragAmount.x,
                        change.position.y - dragAmount.y
                    )
                    tempPath.lineTo(
                        change.position.x,
                        change.position.y
                    )
                    //Здесь обновляется значение path новым путем, созданным на основе tempPath.
                    // Это обновление обеспечивает сохранение пути, нарисованного пользователем.
                    path.value = Path().apply {
                        addPath(tempPath)
                    }
                }
            }
    ){
        // Этот блок кода рисует на холсте путь, который хранится в path.value.
        // Это путь, который будет нарисован на холсте. Это значение изменяется
        // при перемещении пальца по экрану.
        drawPath(
            path.value,
            color = Color.Red,
            style = Stroke(15f)
        )
    }
}