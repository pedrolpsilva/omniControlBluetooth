# =============================================================
#  logcat_omnicontrolbluetooth.ps1
#  Captura logs do OmniControl Bluetooth em tempo real no dispositivo fisico
#
#  USO:
#    .\logcat_omnicontrolbluetooth.ps1            -> mostra tudo (tag OmniControl Bluetooth)
#    .\logcat_omnicontrolbluetooth.ps1 -Full      -> mostra TODOS os logs BT do sistema
#    .\logcat_omnicontrolbluetooth.ps1 -Save      -> salva em log_omnicontrolbluetooth.txt
# =============================================================

param(
    [switch]$Full,   # inclui logs do sistema Bluetooth tambem
    [switch]$Save    # salva em arquivo
)

$device = "c04d378a"
$outFile = "log_omnicontrolbluetooth_$(Get-Date -Format 'yyyyMMdd_HHmmss').txt"

# Limpa o buffer de log antes de comecar
Write-Host "Limpando buffer de logcat..." -ForegroundColor Cyan
adb -s $device logcat -c

Write-Host ""
Write-Host "========================================" -ForegroundColor Yellow
Write-Host "  OmniControl Bluetooth Logcat Monitor" -ForegroundColor Yellow
Write-Host "  Pressione Ctrl+C para parar" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow
Write-Host ""

if ($Full) {
    # Todos os logs relevantes de Bluetooth + OmniControl Bluetooth
    $filter = "OmniControl Bluetooth:V BluetoothHidDevice:V bt_btif:V bt_stack:V *:S"
    Write-Host "Modo: FULL (Bluetooth completo)" -ForegroundColor Magenta
} else {
    # Apenas a tag do app
    $filter = "OmniControl Bluetooth:V *:S"
    Write-Host "Modo: APP apenas (tag OmniControl Bluetooth)" -ForegroundColor Green
}

Write-Host "Filtro: $filter" -ForegroundColor DarkGray
Write-Host ""

if ($Save) {
    Write-Host "Salvando em: $outFile" -ForegroundColor Cyan
    adb -s $device logcat -v time $filter | Tee-Object -FilePath $outFile
} else {
    adb -s $device logcat -v time $filter
}
