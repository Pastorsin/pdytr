#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <math.h>
#include <openssl/md5.h>
#include <stdlib.h>
void error(char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int tam = pow(10, 6);
    int sockfd, portno, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    unsigned char checksum[MD5_DIGEST_LENGTH];
    unsigned char buffer[tam];
    if (argc < 3)
    {
        fprintf(stderr, "usage %s hostname port\n", argv[0]);
        exit(0);
    }
    //TOMA EL NUMERO DE PUERTO DE LOS ARGUMENTOS
    portno = atoi(argv[2]);

    //CREA EL FILE DESCRIPTOR DEL SOCKET PARA LA CONEXION
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    //AF_INET - FAMILIA DEL PROTOCOLO - IPV4 PROTOCOLS INTERNET
    //SOCK_STREAM - TIPO DE SOCKET

    if (sockfd < 0)
        error("ERROR opening socket");

    //TOMA LA DIRECCION DEL SERVER DE LOS ARGUMENTOS
    server = gethostbyname(argv[1]);
    if (server == NULL)
    {
        fprintf(stderr, "ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;

    //COPIA LA DIRECCION IP Y EL PUERTO DEL SERVIDOR A LA ESTRUCTURA DEL SOCKET
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(portno);

    //DESCRIPTOR - DIRECCION - TAMAÑO DIRECCION
    if (connect(sockfd, &serv_addr, sizeof(serv_addr)) < 0)
        error("ERROR connecting");
    bzero(buffer, tam);

    for(int i = 0; i < tam - 1; i++)
        buffer[i] = 'a';
    
    printf("Sin \\0 : ");
    printf("%d - %d \n", strlen(buffer), tam);

    buffer[tam - 1] = '\0';

    printf("Con \\0 : ");
    printf("%d - %d \n", strlen(buffer), tam);

    //Se calcula y envía el tamaño del mensaje
    int converted = htonl(tam);
    n = write(sockfd, &converted, sizeof(converted));
    if (n < 0) error("ERROR writing to socket");

    /* Calcular checksum */
    unsigned char output1[MD5_DIGEST_LENGTH];

    MD5_CTX md5;
    MD5_Init(&md5);
    MD5_Update(&md5, buffer, strlen(buffer));
    MD5_Final(output1, &md5);

    printf("El checksum es: ");
    for (int i = 0; i < MD5_DIGEST_LENGTH; i++)
        printf("%02x", output1[i]);
    printf("\n");

    n = write(sockfd, output1, MD5_DIGEST_LENGTH);
    if (n < 0) error("ERROR writing to socket");

    //Se envía el mensaje
    n = write(sockfd, buffer, tam);
    if (n < 0) error("ERROR writing to socket");
    bzero(buffer, tam);

    //ESPERA RECIBIR UNA RESPUESTA
    n = read(sockfd, buffer, tam - 1);
    if (n < 0)
        error("ERROR reading from socket");
    printf("%s\n", buffer);
    return 0;
}
