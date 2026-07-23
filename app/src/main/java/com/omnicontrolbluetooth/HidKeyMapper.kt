package com.omnicontrolbluetooth

object HidKeyMapper {

    const val MODIFIER_NONE: Byte = 0x00
    const val MODIFIER_LEFT_CTRL: Byte = 0x01
    const val MODIFIER_LEFT_SHIFT: Byte = 0x02
    const val MODIFIER_LEFT_ALT: Byte = 0x04
    const val MODIFIER_LEFT_GUI: Byte = 0x08
    const val MODIFIER_RIGHT_CTRL: Byte = 0x10
    const val MODIFIER_RIGHT_SHIFT: Byte = 0x20
    const val MODIFIER_RIGHT_ALT: Byte = 0x40
    const val MODIFIER_RIGHT_GUI: Byte = 0x80.toByte()

    const val KEY_ENTER: Byte = 0x28
    const val KEY_ESCAPE: Byte = 0x29
    const val KEY_BACKSPACE: Byte = 0x2A
    const val KEY_TAB: Byte = 0x2B
    const val KEY_SPACE: Byte = 0x2C

    data class HidKey(val modifier: Byte, val keycode: Byte)

    fun getHidCode(char: Char): HidKey? {
        return when (char) {
            in 'a'..'z' -> HidKey(MODIFIER_NONE, (0x04 + (char - 'a')).toByte())
            in 'A'..'Z' -> HidKey(MODIFIER_LEFT_SHIFT, (0x04 + (char - 'A')).toByte())
            in '1'..'9' -> HidKey(MODIFIER_NONE, (0x1E + (char - '1')).toByte())
            '0' -> HidKey(MODIFIER_NONE, 0x27)
            ' ' -> HidKey(MODIFIER_NONE, KEY_SPACE)
            '\n', '\r' -> HidKey(MODIFIER_NONE, KEY_ENTER)
            '\t' -> HidKey(MODIFIER_NONE, KEY_TAB)

            '!' -> HidKey(MODIFIER_LEFT_SHIFT, 0x1E)
            '@' -> HidKey(MODIFIER_LEFT_SHIFT, 0x1F)
            '#' -> HidKey(MODIFIER_LEFT_SHIFT, 0x20)
            '$' -> HidKey(MODIFIER_LEFT_SHIFT, 0x21)
            '%' -> HidKey(MODIFIER_LEFT_SHIFT, 0x22)
            '^' -> HidKey(MODIFIER_LEFT_SHIFT, 0x23)
            '&' -> HidKey(MODIFIER_LEFT_SHIFT, 0x24)
            '*' -> HidKey(MODIFIER_LEFT_SHIFT, 0x25)
            '(' -> HidKey(MODIFIER_LEFT_SHIFT, 0x26)
            ')' -> HidKey(MODIFIER_LEFT_SHIFT, 0x27)
            '-' -> HidKey(MODIFIER_NONE, 0x2D)
            '_' -> HidKey(MODIFIER_LEFT_SHIFT, 0x2D)
            '=' -> HidKey(MODIFIER_NONE, 0x2E)
            '+' -> HidKey(MODIFIER_LEFT_SHIFT, 0x2E)
            '[' -> HidKey(MODIFIER_NONE, 0x2F)
            '{' -> HidKey(MODIFIER_LEFT_SHIFT, 0x2F)
            ']' -> HidKey(MODIFIER_NONE, 0x30)
            '}' -> HidKey(MODIFIER_LEFT_SHIFT, 0x30)
            '\\' -> HidKey(MODIFIER_NONE, 0x31)
            '|' -> HidKey(MODIFIER_LEFT_SHIFT, 0x31)
            ';' -> HidKey(MODIFIER_NONE, 0x33)
            ':' -> HidKey(MODIFIER_LEFT_SHIFT, 0x33)
            '\'' -> HidKey(MODIFIER_NONE, 0x34)
            '"' -> HidKey(MODIFIER_LEFT_SHIFT, 0x34)
            '`' -> HidKey(MODIFIER_NONE, 0x35)
            '~' -> HidKey(MODIFIER_LEFT_SHIFT, 0x35)
            ',' -> HidKey(MODIFIER_NONE, 0x36)
            '<' -> HidKey(MODIFIER_LEFT_SHIFT, 0x36)
            '.' -> HidKey(MODIFIER_NONE, 0x37)
            '>' -> HidKey(MODIFIER_LEFT_SHIFT, 0x37)
            '/' -> HidKey(MODIFIER_NONE, 0x38)
            '?' -> HidKey(MODIFIER_LEFT_SHIFT, 0x38)

            'á', 'à', 'ã', 'â', 'ä' -> HidKey(MODIFIER_NONE, 0x04)
            'Á', 'À', 'Ã', 'Â', 'Ä' -> HidKey(MODIFIER_LEFT_SHIFT, 0x04)
            'é', 'è', 'ê', 'ë' -> HidKey(MODIFIER_NONE, 0x08)
            'É', 'È', 'Ê', 'Ë' -> HidKey(MODIFIER_LEFT_SHIFT, 0x08)
            'í', 'ì', 'î', 'ï' -> HidKey(MODIFIER_NONE, 0x0C)
            'Í', 'Ì', 'Î', 'Ï' -> HidKey(MODIFIER_LEFT_SHIFT, 0x0C)
            'ó', 'ò', 'õ', 'ô', 'ö' -> HidKey(MODIFIER_NONE, 0x12)
            'Ó', 'Ò', 'Õ', 'Ô', 'Ö' -> HidKey(MODIFIER_LEFT_SHIFT, 0x12)
            'ú', 'ù', 'û', 'ü' -> HidKey(MODIFIER_NONE, 0x18)
            'Ú', 'Ù', 'Û', 'Ü' -> HidKey(MODIFIER_LEFT_SHIFT, 0x18)
            'ç' -> HidKey(MODIFIER_NONE, 0x06)
            'Ç' -> HidKey(MODIFIER_LEFT_SHIFT, 0x06)

            else -> null
        }
    }
}

