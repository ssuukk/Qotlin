# Qotlin

This is a Kotlin library for developing, deploying and running quantum assembler (QASM) programs on quantum computers available in IBM Q Experience program.

See below to get a taste:

```kotlin
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val qex = Qotlin()

        qex.login("YOUR_API_TOKEN_HERE")

        qex.enumerateDevices()

        println("Currently available:")
        qex.devices.forEach {
            println(it)
        }

        println("\nRunning Bell state experiment")
        qex.simulator
                .submitJob(256, 1, qasm {
                    qreg(2)
                    creg(5)
                    h(0)
                    cx(0, 1)
                    measure(0, 1)
                    measure(1, 1)
                })
                .onStatus(60,
                        {
                            finishedJob ->
                            finishedJob.qasms?.forEach {
                                qasm ->
                                println(qasm.result)
                            }
                        },
                        {
                            println("Job failed")
                        }
                )

        println("Running quantum Fourier transform")
        //qex.devices.firstOrNull { !it.simulator }
        qex.simulator
                ?.submitJob(256,100, qasm {
                    // quantum Fourier transform
                    qreg(4)
                    creg(4)
                    x(0)
                    x(2)
                    barrier()
                    h(0)
                    cu1(Math.PI/2,1,0)
                    h(1)
                    cu1(Math.PI/4,2,0)
                    cu1(Math.PI/2,2,1)
                    h(2)
                    cu1(Math.PI/8,3,0)
                    cu1(Math.PI/4,3,1)
                    cu1(Math.PI/2,3,2)
                    h(3)
                    measure()
                })
                ?.onStatus(500,{
                    finishedJob ->
                    finishedJob.qasms?.forEach {
                        qasm ->
                        println(qasm.result)
                    }
                },{

                })

    }
}
```

### Prerequisites

Obtain an API key at (https://quantumexperience.ng.bluemix.net/qx/experience)

## Built With

* [Gradle](https://gradle.org/)
## Authors

* **ssuukk** - *Initial work*

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE.md) file for details
