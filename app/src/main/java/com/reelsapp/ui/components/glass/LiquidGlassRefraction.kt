package com.reelsapp.ui.components.glass

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

// language=AGSL
private const val LIQUID_GLASS_SHADER = """
    uniform shader content;
    uniform float2 resolution;
    uniform float cornerRadius;
    uniform float2 lightPos;   // -1..1 normalized, from device tilt
    uniform float time;

    float sdRoundRect(float2 p, float2 halfSize, float r) {
        float2 q = abs(p) - halfSize + r;
        return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
    }

    half4 main(float2 fragCoord) {
        float2 center = resolution * 0.5;
        float2 p = fragCoord - center;
        float2 halfSize = resolution * 0.5;
        float dist = sdRoundRect(p, halfSize, cornerRadius);

        // Refraction band: only displace samples within ~18px of the edge,
        // pulling them toward center to fake light bending through the rim.
        float edgeBand = 18.0;
        float edgeT = clamp((dist + edgeBand) / edgeBand, 0.0, 1.0); // 0 at edge, 1 inward
        float pull = (1.0 - edgeT) * 7.0;
        float2 dir = length(p) > 0.001 ? normalize(p) : float2(0.0, 0.0);
        float2 sampleCoord = fragCoord - dir * pull;

        half4 base = content.eval(sampleCoord);

        // Specular hotspot that tracks lightPos (device tilt).
        float2 lightScreen = center + lightPos * halfSize;
        float lightDist = length(fragCoord - lightScreen);
        float highlight = exp(-(lightDist * lightDist) / 9000.0) * 0.30;

        // Thin bright rim right at the border, like a lit glass edge.
        float rim = smoothstep(2.5, 0.0, abs(dist)) * 0.35;

        half3 color = base.rgb + half3(highlight + rim);
        return half4(color, base.a);
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Modifier.liquidGlassRefraction(
    cornerRadiusPx: () -> Float,
    lightPosition: State<Offset>,
): Modifier = this.then(
    Modifier.graphicsLayer {
        val shader = RuntimeShader(LIQUID_GLASS_SHADER)
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("cornerRadius", cornerRadiusPx())
        val lp = lightPosition.value
        shader.setFloatUniform("lightPos", lp.x, lp.y)
        shader.setFloatUniform("time", (System.nanoTime() / 1_000_000_000f) % 1000f)

        renderEffect = RenderEffect
            .createRuntimeShaderEffect(shader, "content")
            .asComposeRenderEffect()
    },
)

@Composable
fun rememberGlassLightPosition(biasX: Float = -0.25f, biasY: Float = -0.4f): State<Offset> {
    val context = LocalContext.current
    val position = remember { mutableStateOf(Offset(biasX, biasY)) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val tiltX = (event.values[0] / 9.8f).coerceIn(-1f, 1f)
                val tiltY = (event.values[1] / 9.8f).coerceIn(-1f, 1f)
                position.value = Offset(
                    x = (-tiltX + biasX).coerceIn(-1f, 1f),
                    y = (tiltY + biasY).coerceIn(-1f, 1f),
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager.unregisterListener(listener) }
    }

    return position
}
