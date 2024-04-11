import java.util.*

var count = 100000
var tempoGlobal: Double = 1.5
val escalonador = PriorityQueue<Evento>()
var eventoAnteriorTempoGlobal = tempoGlobal
val maiorCapacidade = 5

val fila1 = Fila(
    filaCapacidade = 3,
    servidores = 2,
    filaCount = 0,
    perdaCount = 0,
    minChegada = 1,
    maxChegada = 4,
    minSaida = 3,
    maxSaida = 4,
)

val fila2 = Fila(
    filaCapacidade = 5,
    servidores = 1,
    filaCount = 0,
    perdaCount = 0,
    minChegada = 0,
    maxChegada = 0,
    minSaida = 2,
    maxSaida = 3,
)

fun main(args: Array<String>) {

    escalonador.add(Evento(TIPO_EVENTO.CHEGADA, tempoDemandado(fila1.minChegada, fila1.maxChegada)))

    while (count > 0) {
        val evento = proximoEvento()

        when (evento.tipo) {
            TIPO_EVENTO.CHEGADA -> chegada(evento)
            TIPO_EVENTO.SAIDA -> saida(evento)
            TIPO_EVENTO.PASSAGEM -> passagem(evento)
        }
        count--
    }

    fila1.printResultado()
    println()
    fila2.printResultado()
}

fun acumulaTempo(evento: Evento, filaCount: Int){

    var tempoDeDiferenca = evento.tempo - eventoAnteriorTempoGlobal
    eventoAnteriorTempoGlobal = evento.tempo
    fila1.tempoNClientesFila[filaCount] += tempoDeDiferenca
    fila2.tempoNClientesFila[filaCount] += tempoDeDiferenca

}

fun chegada(evento: Evento) {
    tempoGlobal = evento.tempo
    acumulaTempo(evento, fila1.filaCount)

    if (fila1.filaCount < fila1.filaCapacidade) {
        fila1.filaCount++
        if (fila1.filaCount <= fila1.servidores) {
            agendaEvento(Evento(TIPO_EVENTO.PASSAGEM, tempoGlobal + tempoDemandado(fila1.minSaida,fila1.maxSaida)))
        }
    } else {
        fila1.perdaCount++
    }
    agendaEvento(Evento(TIPO_EVENTO.CHEGADA, tempoGlobal + tempoDemandado(fila1.minChegada,fila1.maxChegada)))
}

fun saida(evento: Evento) {
    tempoGlobal = evento.tempo
    acumulaTempo(evento, fila2.filaCount)

    fila2.filaCount--
    if (fila2.filaCount >= fila2.servidores) {
        agendaEvento(Evento(TIPO_EVENTO.SAIDA, tempoGlobal + tempoDemandado(fila2.minSaida,fila2.minChegada)))
    }
}

fun passagem(evento: Evento) {
    tempoGlobal = evento.tempo
    if (fila1.filaCount >= fila1.filaCapacidade) {
        agendaEvento(Evento(TIPO_EVENTO.PASSAGEM, tempoGlobal + tempoDemandado(fila1.minSaida,fila1.maxSaida) ))
    }
    if (fila2.filaCount < fila2.filaCapacidade) {
        fila2.filaCount++
        if (fila2.filaCount <= fila2.servidores) {
            agendaEvento(Evento(TIPO_EVENTO.SAIDA, tempoGlobal + tempoDemandado(fila2.minSaida,fila2.maxSaida) ))
        }
    } else {
        fila2.perdaCount++
    }
}

fun proximoEvento(): Evento = escalonador.poll()

fun agendaEvento(evento: Evento) {
    escalonador.add(evento)
}

enum class TIPO_EVENTO() {
    CHEGADA, SAIDA, PASSAGEM
}

object GeradorDeNumero {
    var anterior = 2.0
    val a = 1987571865
    val c = 174153
    val m = Math.pow(2.0, 24.0)

    fun aleatorio(): Double {

        anterior = (a * anterior + c) % m
        return anterior / m
    }
}

fun tempoDemandado(a: Int, b: Int): Double {

    val test = GeradorDeNumero.aleatorio()
    val resultado = a + ((b - a) * test)
    return resultado
}

data class Evento(val tipo: TIPO_EVENTO, val tempo: Double) : Comparable<Evento> {

    override fun compareTo(other: Evento): Int {
        return this.tempo.compareTo(other.tempo)
    }

}

data class Fila(
    val filaCapacidade: Int,
    val servidores: Int,
    var filaCount: Int,
    var perdaCount: Int,
    val minChegada: Int,
    val maxChegada: Int,
    val minSaida: Int,
    val maxSaida: Int,
    val tempoNClientesFila: MutableList<Double> = MutableList(maiorCapacidade + 1) { 0.0 }
){
    fun printResultado(){
        println("tempo global: $tempoGlobal")
        println("perdas: $perdaCount")
        tempoNClientesFila.forEachIndexed { index, tempo ->
            println()
            println("pessoas na fila: $index ")
            println("tempo: $tempo")
            println("porcentagem de tempo: ${(tempo / tempoGlobal) * 100}%")
        }
    }
}