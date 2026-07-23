package com.omnicontrolbluetooth.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.AdsClick
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.PanTool
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnicontrolbluetooth.ui.components.atoms.GestureGuideItem

@Composable
fun GestureGuideBottomSheetContent(
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.HelpOutline,
                null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Guia de Gestos",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            GestureGuideItem(
                icon = Icons.Outlined.PanTool,
                title = "1 dedo — arrastar",
                description = "Move o cursor"
            )
            GestureGuideItem(
                icon = Icons.Outlined.TouchApp,
                title = "1 toque rápido",
                description = "Clique esquerdo"
            )
            GestureGuideItem(
                icon = Icons.Outlined.AdsClick,
                title = "1 toque rápido e segurar",
                description = "Clique e arrasta (arrastar arquivos / seleção)"
            )
            GestureGuideItem(
                icon = Icons.Outlined.Gesture,
                title = "2 dedos — toque",
                description = "Clique direito"
            )
            GestureGuideItem(
                icon = Icons.Default.SwapVert,
                title = "2 dedos — arrastar vertical",
                description = "Scroll vertical"
            )
            GestureGuideItem(
                icon = Icons.Default.ChevronLeft,
                title = "2 dedos — deslizar p/ esquerda",
                description = "Voltar página (navegação)"
            )
            GestureGuideItem(
                icon = Icons.Default.ChevronRight,
                title = "2 dedos — deslizar p/ direita",
                description = "Avançar página (navegação)"
            )
            GestureGuideItem(
                icon = Icons.Default.Fullscreen,
                title = "Fullscreen",
                description = "Expande o touchpad para tela cheia"
            )
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF262626), RoundedCornerShape(16.dp))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Fechar",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

