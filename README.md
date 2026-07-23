# 🖱️ OmniControl Bluetooth (OmniControl Bluetooth)

> Transforme seu smartphone Android em um Mouse (Trackpad) e Teclado Bluetooth universal sem a necessidade de instalar programas no dispositivo receptor.

---

## 🎯 Objetivo do Aplicativo

O **OmniControl Bluetooth** foi desenvolvido para transformar qualquer smartphone Android em um periférico físico sem fio padrão **Bluetooth HID (Human Interface Device)**. 

Diferente de outros aplicativos do mercado, ele se conecta diretamente a computadores (Windows, Mac, Linux), Smart TVs, Android TV, Consoles e Tablets **sem exigir a instalação de qualquer aplicativo ou servidor cliente** no dispositivo de destino. O dispositivo receptor reconhece o celular nativamente como se fosse um mouse e teclado USB/Bluetooth físicos.

---

## 🚀 Tecnologias Utilizadas

O projeto foi construído utilizando as tecnologias mais modernas do ecossistema Android:

* **Android Jetpack Compose**: Interface declarativa moderna, rápida e altamente customizável desenvolvida com componentes **Material Design 3**.
* **Android Bluetooth HID API (`BluetoothHidDevice`)**: Protocolo nativo do Android SDK para emulação direta de dispositivos de entrada USB/HID.
* **Android Foreground Service (`HidForegroundService`)**: Serviço em segundo plano com notificação persistente para garantir conexão estável mesmo quando o app estiver minimizado ou com a tela bloqueada.
* **Kotlin Coroutines & Flow**: Programação assíncrona reativa para gerenciamento fluido de eventos e estados de conexão Bluetooth.
* **Retrofit & Moshi / Firebase AI**: Suporte à conversão de dados e recursos de IA no app.
* **Secrets Gradle Plugin**: Gerenciamento seguro de variáveis de ambiente via arquivo `.env`.
* **Suite de Testes & Roborazzi**: Suporte a testes unitários, testes de UI em Compose e testes visuais de regressão (*Screenshot Testing*) com Roborazzi e Robolectric.

---

## 💻 Linguagem

* **Kotlin** (100% Kotlin): Código limpo, moderno, conciso e fortemente tipado.

---

## 🏗️ Arquitetura e Estrutura do Projeto

O projeto segue a metodologia **Atomic Design** combinada com princípios de **Clean Code**:

```text
app/src/main/java/com/omnicontrolbluetooth/
├── BluetoothHidDeviceManager.kt  # Gerenciador do perfil Bluetooth HID e envio de pacotes HID
├── HidKeyMapper.kt              # Mapeador de caracteres e teclas físicas para relatórios HID USB
├── MainActivity.kt               # Ponto de entrada, inicialização de permissões e tema
├── services/
│   └── HidForegroundService.kt   # Serviço para manter o perfil HID ativo em segundo plano
└── ui/
    ├── components/
    │   ├── atoms/                # Botões de ícones, sliders de sensibilidade, dicas visuais
    │   ├── molecules/            # Header, Footer, caixas de status de dispositivo
    │   └── organisms/            # BottomSheet de lista de dispositivos, teclado, guia de gestos
    ├── screens/
    │   └── MousePadScreen.kt     # Tela principal integrando Trackpad e Controles
    └── theme/                    # Design System, cores, tipografia e temas Material 3
```

---

## ✨ Funcionalidades Principais

* **🖱️ Trackpad Multitouch**:
  * Movimentação precisa do ponteiro em tempo real.
  * Clique simples (botão esquerdo) e clique duplo.
  * Clique com dois dedos (botão direito do mouse).
  * Rolagem (*Scroll*) vertical e horizontal fluida com dois dedos.
  * Controle dinâmico de sensibilidade do ponteiro.
* **⌨️ Teclado Bluetooth Nativo**:
  * Emulação de teclas alfanuméricas e caracteres especiais.
  * Suporte a teclas de controle (`Enter`, `Backspace`, `Tab`, `Esc`, setas direcionais e modificadores).
* **📱 Gerenciamento de Conexão**:
  * Painel inferior (*Bottom Sheet*) para busca, pareamento e reconexão rápida com dispositivos Bluetooth previamente pareados.
  * Feedback visual em tempo real do estado da conexão (`Conectado`, `Desconectado`, `Aguardando`).
* **📖 Guia Interativo de Gestos**:
  * Instruções visuais para aprendizado rápido de todos os gestos do trackpad.

---

## 🛠️ Como Executar o Projeto

### Pré-requisitos
1. **Android Studio** (Ladybug ou superior recomendado).
2. **Dispositivo Físico Android** rodando **Android 8.0 (API level 26)** ou superior (*dispositivos físicos são recomendados pois emuladores geralmente não possuem suporte ao perfil Bluetooth HID*).
3. **JDK 11** configurado no projeto.

### Passo a Passo

1. **Clonar o Repositório**:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd omnicontrolbluetooth
   ```

2. **Configurar Variáveis de Ambiente**:
   Crie um arquivo `.env` na raiz do projeto baseado no `.env.example`:
   ```bash
   cp .env.example .env
   ```

3. **Abrir no Android Studio**:
   * Abra o Android Studio e selecione **Open**.
   * Escolha a pasta do projeto `omnicontrolbluetooth`.
   * Aguarde a sincronização do Gradle.

4. **Compilar e Executar**:
   * Conecte seu dispositivo Android via depuração USB.
   * Conceda as permissões de Bluetooth (`BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`) solicitadas na abertura do app.
   * Selecione o dispositivo e clique em **Run** (`Shift + F10`).

---

## 👤 Desenvolvedor / Contato

* **Nome**: Pedro Luiz Pinheiro Silva
* **E-mail**: [pedro.lpo.oficial@gmail.com](mailto:pedro.lpo.oficial@gmail.com)

---

<div align="center">
  <sub>Desenvolvido com ❤️ utilizando Kotlin e Jetpack Compose</sub>
</div>
