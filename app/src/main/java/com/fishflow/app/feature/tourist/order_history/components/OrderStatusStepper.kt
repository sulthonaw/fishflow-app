// OrderStatusStepper.kt
// Developer: Sulthon
//
// Stepper visual 4 tahap status pesanan. Stateless.
// Terima: currentStatus (OrderStatus)
// Render 4 titik milestone dengan garis penghubung.
// Tahap yang sudah lewat: ikon centang + warna hijau.
// Tahap saat ini: warna biru + animasi pulse.
// Tahap belum: abu-abu.
package com.fishflow.app.feature.tourist.order_history.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishflow.app.core.ui.components.OrderDesignTokens
import com.fishflow.app.data.model.OrderStatus

private enum class StepVisualState { DONE, ACTIVE, UPCOMING }

private data class TrackingStep(
    val title: String,
    val trailingLabel: String,
    val description: String,
    val highlightLocation: String? = null,
    val deadlineLabel: String? = null
)

@Composable
fun OrderStatusStepper(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier,
    sellerShortName: String = "Pak Joko",
    allocatedSummary: String = "3 kg tuna segar dikunci dalam wadah bernomor #A-14.",
    pickupPoint: String = "Pos 3 Dermaga Sadranan",
    pickupDeadline: String = "09:30 WIB"
) {
    val activeStepIndex = when (currentStatus) {
        OrderStatus.MENUNGGU_KONFIRMASI -> 0
        OrderStatus.DIKONFIRMASI -> 1
        OrderStatus.SIAP_DIAMBIL -> 2
        OrderStatus.SELESAI -> 3
        OrderStatus.DITOLAK, OrderStatus.DIBATALKAN -> 0
    }

    val steps = listOf(
        TrackingStep(
            title = "Pesanan Diterima Penjual",
            trailingLabel = "07:15 WIB",
            description = "Penjual $sellerShortName menerima permintaan tangkapan."
        ),
        TrackingStep(
            title = "Stok Dialokasikan",
            trailingLabel = "07:22 WIB",
            description = allocatedSummary
        ),
        TrackingStep(
            title = "Siap Diambil di Dermaga",
            trailingLabel = "Sekarang",
            description = "Silakan datang langsung ke ",
            highlightLocation = pickupPoint,
            deadlineLabel = "Maksimal penjemputan: $pickupDeadline"
        ),
        TrackingStep(
            title = "Penyerahan Selesai & Ditutup",
            trailingLabel = "Tahap Akhir",
            description = "Verifikasi kode pengambilan dan serah terima ikan bersama Penjual."
        )
    )

    val stepDisplayNumber = (activeStepIndex + 1).coerceIn(1, 4)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progres Penjemputan",
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.07).sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(OrderDesignTokens.MintAccent.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Langkah $stepDisplayNumber dari 4",
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.12.sp
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { index, step ->
                    val state = when {
                        currentStatus == OrderStatus.SELESAI -> StepVisualState.DONE
                        index < activeStepIndex -> StepVisualState.DONE
                        index == activeStepIndex -> StepVisualState.ACTIVE
                        else -> StepVisualState.UPCOMING
                    }

                    TimelineStepRow(
                        step = step,
                        state = state,
                        isLast = index == steps.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineStepRow(
    step: TrackingStep,
    state: StepVisualState,
    isLast: Boolean
) {
    val pulseTransition = rememberInfiniteTransition(label = "stepPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .alpha(if (state == StepVisualState.UPCOMING) 0.6f else 1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp)
            ) {
                if (state == StepVisualState.ACTIVE) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(OrderDesignTokens.MintAccent)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when (state) {
                                StepVisualState.DONE,
                                StepVisualState.ACTIVE -> OrderDesignTokens.CoastalTeal
                                StepVisualState.UPCOMING -> OrderDesignTokens.SurfaceContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        StepVisualState.DONE -> Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        StepVisualState.ACTIVE -> Icon(
                            imageVector = Icons.Rounded.Anchor,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        StepVisualState.UPCOMING -> Icon(
                            imageVector = Icons.Rounded.DoneAll,
                            contentDescription = null,
                            tint = OrderDesignTokens.TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp)
                        .background(
                            if (state == StepVisualState.UPCOMING) {
                                OrderDesignTokens.SurfaceContainer
                            } else {
                                OrderDesignTokens.MintAccent
                            }
                        )
                )
            }
        }

        val bottomGap = if (isLast) 0.dp else 20.dp

        if (state == StepVisualState.ACTIVE) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = bottomGap)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrderDesignTokens.SurfaceMuted)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.title,
                        color = OrderDesignTokens.CoastalTeal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.07).sp,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(OrderDesignTokens.CoastalTeal)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = step.trailingLabel,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = buildAnnotatedString {
                        append(step.description)
                        if (step.highlightLocation != null) {
                            withStyle(SpanStyle(color = OrderDesignTokens.CoastalTeal, fontWeight = FontWeight.Medium)) {
                                append(step.highlightLocation)
                            }
                            append(".")
                        }
                    },
                    color = OrderDesignTokens.TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                if (step.deadlineLabel != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = OrderDesignTokens.WarningText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = step.deadlineLabel,
                            color = OrderDesignTokens.WarningText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = bottomGap),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = step.title,
                        color = OrderDesignTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.07).sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = step.trailingLabel,
                        color = OrderDesignTokens.TextMuted,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = buildAnnotatedString {
                        append(step.description)
                        if (step.highlightLocation != null) {
                            append(step.highlightLocation)
                            append(".")
                        }
                    },
                    color = if (state == StepVisualState.UPCOMING) {
                        OrderDesignTokens.TextMuted
                    } else {
                        OrderDesignTokens.TextSecondary
                    },
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
