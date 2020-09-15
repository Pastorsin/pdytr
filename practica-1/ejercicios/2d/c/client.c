#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <math.h>
#include <openssl/md5.h>
#include <stdlib.h>

#ifndef BUFFER_SIZE
#define BUFFER_SIZE 10000000
#endif

void error(char *msg)
{
    perror(msg);
    exit(0);
}

double dwalltime()
{
    double sec;
    struct timeval tv;

    gettimeofday(&tv, NULL);
    sec = tv.tv_sec + tv.tv_usec / 1000000.0;
    return sec;
}

int main(int argc, char *argv[])
{
    static unsigned char bufferIn[BUFFER_SIZE];
    static unsigned char bufferOut[BUFFER_SIZE];
    int sockfd, portno, n, i;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    unsigned char checksum[MD5_DIGEST_LENGTH];
    double timetick;
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
    bzero(bufferIn, BUFFER_SIZE);

    /* Cargo el bufferIn en todas sus posiciones con el carácter 'a'.
    En la última posición indico el fin del string con '\0'.
    */
    for(i = 0; i < BUFFER_SIZE - 1; i++)
        bufferIn[i] = 'a';
    bufferIn[BUFFER_SIZE - 1] = '\0';

    //Calcular el checksum
    MD5(bufferIn, BUFFER_SIZE, checksum);

    // Se calcula y envía el tamaño del mensaje en los primeros 4 bytes
    int converted = htonl(BUFFER_SIZE);

    //empieza el calculo del tiempo
    timetick = dwalltime();
    n = write(sockfd, &converted, sizeof(converted));
    if (n < 0) error("ERROR writing to socket");

    // Se envía el checksum
    n = write(sockfd, checksum, MD5_DIGEST_LENGTH);
    if (n < 0) error("ERROR writing to socket");

    //Se envía el mensaje
    n = write(sockfd, bufferIn, BUFFER_SIZE);
    if (n < 0) error("ERROR writing to socket");

    //ESPERA RECIBIR UNA RESPUESTA
    n = read(sockfd, bufferOut, BUFFER_SIZE);    
    if (n < 0) error("ERROR reading from socket");
    printf("%f\n", dwalltime() - timetick);

    return 0;
}
