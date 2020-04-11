package ru.skillbranch.devintensive.models

import java.util.regex.Pattern

class Bender(var status: Status = Status.NORMAL,
             var question: Question = Question.NAME) {

    var retries: Int = 0
    var maxRetries: Int = 3

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>, val error: String?) {
        NAME("Как меня зовут?", listOf("бендер", "bender"), "Имя должно начинаться с заглавной буквы") {
            override fun nextQuestion(): Question = PROFESSION
            override fun ensureAnswer(answer: String): String? {
                var symbol = answer[0].toLowerCase()
                if (symbol == answer[0])
                    return error

                return null
            }
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender"), "Профессия должна начинаться со строчной буквы") {
            override fun nextQuestion(): Question = MATERIAL
            override fun ensureAnswer(answer: String): String? {
                var symbol = answer[0].toUpperCase()
                if (symbol == answer[0])
                    return error
                return null
            }
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood"), "Материал не должен содержать цифр") {
            override fun nextQuestion(): Question = BDAY
            override fun ensureAnswer(answer: String): String? {
                val mathcer = Pattern.compile("^\\D*$").matcher(answer);

                if (!mathcer.matches())
                    return error

                return null
            }
        },
        BDAY("Когда меня создали?", listOf("2993"), "Год моего рождения должен содержать только цифры") {
            override fun nextQuestion(): Question = SERIAL
            override fun ensureAnswer(answer: String): String? {
                for (sym in answer) {
                    if (sym < '0' || sym > '9')
                        return error
                }

                return null
            }
        },
        SERIAL("Мой серийный номер?", listOf("2716057"), "Серийный номер содержит только цифры, и их 7") {
            override fun nextQuestion(): Question = IDLE
            override fun ensureAnswer(answer: String): String? {
                for (sym in answer) {
                    if (sym < '0' || sym > '9')
                        return error
                }

                if (answer.length > 7)
                    return error

                return null
            }
        },
        IDLE("На этом все, вопросов больше нет", listOf(), null) {
            override fun nextQuestion(): Question = IDLE
            override fun ensureAnswer(answer: String): String? {
                return null
            }
        };

        abstract fun nextQuestion(): Question
        abstract fun ensureAnswer(answer: String): String?
    }

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }


    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        return if (question.ensureAnswer(answer) != null) {
            (question.error + "\n" + question.question) to status.color
        } else if (question.name.equals(Question.IDLE.name)) {
            "Отлично - ты справился\n" +
                    question.question to status.color
        } else if (question.answers.contains(answer.toLowerCase())) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${(question.question)}" to status.color
        } else {
            status = status.nextStatus()
            retries++

            if (retries > maxRetries) {
                question = Question.NAME
                status = Status.NORMAL
                retries = 0
                "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            } else {
                "Это неправильный ответ\n${question.question}" to status.color
            }
        }

    }

}