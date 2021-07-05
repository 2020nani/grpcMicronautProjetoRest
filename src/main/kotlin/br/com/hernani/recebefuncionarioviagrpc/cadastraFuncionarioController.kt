package br.com.hernani.recebefuncionarioviagrpc

import br.com.hernani.Cargo
import br.com.hernani.FuncionarioRequest
import br.com.hernani.FuncionarioServiceGrpc
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import org.slf4j.LoggerFactory
import javax.inject.Inject

@Controller
class cadastraFuncionarioController(
    @Inject val grpcClient: FuncionarioServiceGrpc.FuncionarioServiceBlockingStub
) {
    //instancia logger
    val logger = LoggerFactory.getLogger(cadastraFuncionarioController::class.java)

    @Get("/api/funcionario")
    fun cadastraFuncionario(
        @QueryValue nome: String,
        @QueryValue cpf: String,
        @QueryValue idade: Int,
        @QueryValue cargo: Cargo
    ): String {
        logger.info("Iniciando requisicao")
        val request = FuncionarioRequest.newBuilder()
            .setNome(nome)
            .setCpf(cpf)
            .setIdade(idade)
            .setCargo(cargo)
            .build()

        val response = grpcClient.cadastraFuncionario(request);
        logger.info("requisicao finalizada com sucesso, id funcionario: $response")
        return "Funcionario cadastrado com sucesso, ID: $response"
    }
}


