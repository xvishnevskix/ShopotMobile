package org.videotrade.shopot.presentation.components.Common

import DescriptionInput
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.resources.compose.stringResource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getAppLaunchCount
import org.videotrade.shopot.api.getFirstLaunchDate
import org.videotrade.shopot.api.getLastRateShown
import org.videotrade.shopot.api.getLastSurveyShown
import org.videotrade.shopot.api.getRatePostpone
import org.videotrade.shopot.api.getSurveyPostpone
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.incrementAppLaunchCounter
import org.videotrade.shopot.api.isAppRated
import org.videotrade.shopot.api.isSurveyHidden
import org.videotrade.shopot.api.markAppRated
import org.videotrade.shopot.api.saveLastRateShown
import org.videotrade.shopot.api.saveLastSurveyShown
import org.videotrade.shopot.api.saveRatePostpone
import org.videotrade.shopot.api.saveSurveyPostpone
import org.videotrade.shopot.api.setFirstLaunchDateIfNotSet
import org.videotrade.shopot.api.setSurveyHidden
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.profile.ProfileViewModel
import sendEmail
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo

data class RatedQuestion(
    val question: String,
    var rating: MutableState<Int> = mutableStateOf(0),
    var comment: MutableState<String> = mutableStateOf("")
)

@kotlinx.serialization.Serializable
data class AppSurveyDto(
    val id: String,
    val questions: List<String>
)

suspend fun getSurvey(): AppSurveyDto? {
    return try {
        println("survey get")
        val client = HttpClient(getHttpClientEngine())
        val response = client.get("${EnvironmentConfig.SERVER_URL}news/surveys")
        val text = response.bodyAsText()
        val surveyList = Json.decodeFromString<List<AppSurveyDto>>(text)
        surveyList.firstOrNull()
    } catch (e: Exception) {
        println("Error survey get: $e")
        null
    }
}

suspend fun sendAppRating(userId: String, rating: Int, description: String): HttpResponse? {
    val client = HttpClient(getHttpClientEngine())

    try {
        val payload = buildJsonObject {
            put("userId", userId)
            put("rating", rating)
            put("description", description)
        }.toString()

        println("Sending app rating: $payload")

        val response: HttpResponse = client.post("${EnvironmentConfig.SERVER_URL}news/rating") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        println("Rating response: ${response.bodyAsText()}")

        return if (response.status.isSuccess()) response else null

    } catch (e: Exception) {
        println("Error sending app rating: $e")
        return null
    } finally {
        client.close()
    }
}

suspend fun sendSurveyResponse(
    userId: String,
    questionAnswers: List<Triple<String, String, Int>>
): HttpResponse? {
    val client = HttpClient(getHttpClientEngine())

    try {
        val jsonAnswers = buildJsonArray {
            questionAnswers.forEach { (question, answer, rating) ->
                add(buildJsonObject {
                    put("question", question)
                    put("answer", answer)
                    put("rating", rating)
                })
            }
        }

        val payload = buildJsonObject {
            put("userId", userId)
            put("questionAnswers", jsonAnswers)
        }.toString()

        println("Sending survey response: $payload")

        val response: HttpResponse = client.post("${EnvironmentConfig.SERVER_URL}news/survey/response") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        println("Survey response: ${response.bodyAsText()}")

        return if (response.status.isSuccess()) response else null

    } catch (e: Exception) {
        println("Error sending survey: $e")
        return null
    } finally {
        client.close()
    }
}


@Composable
fun SurveyDialog(
    survey: AppSurveyDto,
    onDismiss: () -> Unit
) {
    val profileViewModel: ProfileViewModel = koinInject()
    val profile = profileViewModel.profile.collectAsState().value
    val colors = MaterialTheme.colorScheme
    val questions = remember {
        survey.questions.map { RatedQuestion(it) }.toMutableStateList()
    }

    val currentPage = remember { mutableStateOf(0) }
    val showIntro = remember { mutableStateOf(true) }
    val isSubmitting = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isSuccessfulSend = remember { mutableStateOf(false) }
    val isMessageSent = remember { mutableStateOf(false) }

    LaunchedEffect(isMessageSent.value) {
        if (isMessageSent.value) {
            println("close dialog")
            delay(3000)
            onDismiss()
        }
    }

    println("profileeeee ${profile}")

    Dialog(
        onDismissRequest = {
            incrementAppLaunchCounter()
            saveSurveyPostpone()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(32.dp)
        ) {
            if (!isMessageSent.value) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (showIntro.value) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(MokoRes.strings.how_do_you_like_app),
                            fontSize = 24.sp,
                            lineHeight = 24.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            textAlign = TextAlign.Center,
                            color = colors.primary,

                            )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            stringResource(MokoRes.strings.short_survey_invite),
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            textAlign = TextAlign.Center,
                            color = colors.secondary,

                            )
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomButton(stringResource(MokoRes.strings.start_survey), {
                            showIntro.value = false
                        }, style = ButtonStyle.Gradient)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(MokoRes.strings.not_now), modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                saveSurveyPostpone()
                                incrementAppLaunchCounter()
                                onDismiss()
                            }, color = colors.secondary)

                            Text(stringResource(MokoRes.strings.do_not_show_again), modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                setSurveyHidden(survey.id)
                                onDismiss()
                            }, color = colors.secondary
                            )
                        }



                    } else {
                        val pageQuestions = questions.drop(currentPage.value * 3).take(3)

                        pageQuestions.forEach { q ->
                            Column(modifier = Modifier.fillMaxWidth() ,horizontalAlignment = Alignment.Start) {
                                Text(
                                    q.question,
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    textAlign = TextAlign.Center,
                                    color = colors.primary,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(Modifier.align(Alignment.CenterHorizontally)) {
                                    repeat(5) { i ->
                                        val starSelected = i < q.rating.value
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (starSelected) Color(0xFFFFD369) else colors.secondary,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null // Remove click effect
                                                ) { q.rating.value = i + 1 }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                BasicTextField(
                                    cursorBrush = SolidColor(colors.primary),
                                    value = q.comment.value,
                                    onValueChange = { q.comment.value = it },
                                    decorationBox = { innerTextField ->
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            if (q.comment.value.isEmpty()) {
                                                Text(
                                                    stringResource(MokoRes.strings.enter_comment_optional),
                                                    fontSize = 14.sp,
                                                    lineHeight = 14.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                    fontWeight = FontWeight(400),
                                                    textAlign = TextAlign.Start,
                                                    color = colors.secondary
                                                )
                                            }
                                            innerTextField()
                                        }
                                    },
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        textAlign = TextAlign.Start,
                                        color = colors.primary
                                    ),
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colors.secondaryContainer,
                                            shape = RoundedCornerShape(size = 16.dp)
                                        )
                                        .fillMaxWidth()
                                        .background(colors.background)
                                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))


                        val hasNextPage = (currentPage.value + 1) * 3 < questions.size
                        val currentPageQuestions = questions.drop(currentPage.value * 3).take(3)
                        val allCurrentAnswered = currentPageQuestions.all { it.rating.value > 0 }

                        if (!hasNextPage) {

                            CustomButton(
                                stringResource(MokoRes.strings.send),

                                {
                                    coroutineScope.launch {
                                        isSubmitting.value = true
//                                        val message = buildString {
//                                            append("Результаты опроса пользователя ${profile.login}:\n")
//                                            questions.forEachIndexed { i, q ->
//                                                append("${i + 1}. ${q.question} — ${q.rating.value}⭐\n")
//                                                if (q.comment.value != "") {
//                                                    append("Комментарий: ${q.comment.value}\n")
//                                                }
//                                                append("\n")
//                                            }
//                                        }
//                                       val response = sendEmail("survey@videotrade.ru", message)

                                        val answers = questions.map {
                                            Triple(it.question, it.comment.value, it.rating.value)
                                        }
                                        val response = sendSurveyResponse(profile.id, answers)

                                        isSubmitting.value = false
                                        isSuccessfulSend.value = response != null && response.status.isSuccess()
                                        isMessageSent.value = true

                                        if (isSuccessfulSend.value) {
                                            println("sdfsdfsdfsdfsdf")
                                            setSurveyHidden(survey.id)
                                        } else {
                                            incrementAppLaunchCounter()
                                        }
                                        saveLastSurveyShown(Clock.System.now().toEpochMilliseconds())

                                    }

                                },
                                style = ButtonStyle.Gradient,
                                disabled = !allCurrentAnswered,
                                isLoading = isSubmitting.value,
                            )

                        } else  {
                            CustomButton(stringResource(MokoRes.strings.next), {
                                if ((currentPage.value + 1) * 3
                                    < questions.size)
                                    currentPage.value += 1
                            },
                                style = ButtonStyle.Gradient,
                                disabled = !allCurrentAnswered)

                        }
                    }
                }

            } else {


                if (isSuccessfulSend.value) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .background(
                                color = colors.background,
                                shape = RoundedCornerShape(size = 16.dp)
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(width = 128.dp, height = 86.dp),
                            painter = painterResource(Res.drawable.auth_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(colors.primary)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            stringResource(MokoRes.strings.request_accepted),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            textAlign = TextAlign.Center,
                            color = colors.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(MokoRes.strings.your_feedback),
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            textAlign = TextAlign.Center,
                            color = colors.secondary,
                            maxLines = 3,
                        )
//                        Spacer(modifier = Modifier.height(24.dp))
//                        CustomButton(stringResource(MokoRes.strings.close), {
//                            onDismiss()
//                        }, style = ButtonStyle.Gradient)
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(MokoRes.strings.an_error_occurred_please_try_again),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = colors.primary,
                            modifier = Modifier
                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        CustomButton(stringResource(MokoRes.strings.close), {
//                            onDismiss()
//                        }, style = ButtonStyle.Gradient)
                    }
                }
            }
        }
    }
}

@Composable
fun RateAppDialog(
    onDismiss: () -> Unit
) {
    val profileViewModel: ProfileViewModel = koinInject()
    val profile = profileViewModel.profile.collectAsState().value
    val rating = remember { mutableStateOf(0) }
    val description = remember { mutableStateOf("") }
    val isSubmitting = remember { mutableStateOf(false) }
    val isSuccessfulSend = remember { mutableStateOf(false) }
    val isMessageSent = remember { mutableStateOf(false) }
    val isDescValid = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    val surveyKey = "rate_app"

    LaunchedEffect(isMessageSent.value) {
        if (isMessageSent.value) {
            delay(3000)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = {
            incrementAppLaunchCounter()
            saveRatePostpone()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            if (!isMessageSent.value) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(MokoRes.strings.rate_our_app),
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = colors.primary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ⭐ Звезды
                    Row {
                        repeat(5) { i ->
                            val selected = i < rating.value
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (selected) Color(0xFFFFD369) else colors.secondary,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { rating.value = i + 1 }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 📝 Инпут для отзыва
                    DescriptionInput(description,isDescValid.value)

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomButton(
                        stringResource(MokoRes.strings.send),
                        isLoading = isSubmitting.value,
                        onClick = {


                            coroutineScope.launch {
                                isSubmitting.value = true
//                                val message = buildString {
//                                    append("Оценка от пользователя ${profile.login}: ${rating.value}⭐\n")
//                                    append("Отзыв: ${description.value}")
//                                }
//                                val response = sendEmail("survey@videotrade.ru", message)

                                val response = sendAppRating(profile.id, rating.value, description.value)

                                isSubmitting.value = false
                                isSuccessfulSend.value = response != null && response.status.isSuccess()
                                isMessageSent.value = true


                                incrementAppLaunchCounter()

                                if (isSuccessfulSend.value) {
                                    saveLastRateShown(Clock.System.now().toEpochMilliseconds())
                                    if (rating.value > 0) {
                                        markAppRated()
                                    }
                                }
                            }
                        },
                        style = ButtonStyle.Gradient
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(MokoRes.strings.not_now), modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            saveRatePostpone()
                            incrementAppLaunchCounter()
                            onDismiss()
                        }, color = colors.secondary)

                        Text(stringResource(MokoRes.strings.do_not_show_again), modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            setSurveyHidden(surveyKey)
                            onDismiss()
                        }, color = colors.secondary)
                    }
                }
            } else {
                if (isSuccessfulSend.value) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .background(color = colors.background, shape = RoundedCornerShape(size = 16.dp)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(width = 128.dp, height = 86.dp),
                            painter = painterResource(Res.drawable.auth_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(colors.primary)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            stringResource(MokoRes.strings.thank_you_for_feedback),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            textAlign = TextAlign.Center,
                            color = colors.primary
                        )
                    }


                } else {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(MokoRes.strings.an_error_occurred_please_try_again),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            color = colors.primary
                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        CustomButton(stringResource(MokoRes.strings.close), {
//                            onDismiss()
//                        }, style = ButtonStyle.Gradient)
                    }
                }
            }
        }
    }
}


//@Composable
//fun CheckAndShowSurvey() {
//    val coroutineScope = rememberCoroutineScope()
//    val showSurveyDialog = remember { mutableStateOf<AppSurveyDto?>(null) }
//
//    LaunchedEffect(Unit) {
//
//        val count = getAppLaunchCount()
//        val survey = getSurvey()
//
//        println("count: ${count}")
//        if (count % 1 == 0 && survey != null && !isSurveyHidden(survey.id)) {
//            showSurveyDialog.value = survey
//        }
//    }
//
//    showSurveyDialog.value?.let { survey ->
//        SurveyDialog(survey = survey, onDismiss = {
//            showSurveyDialog.value = null
//        })
//    }
//}
//
//@Composable
//fun CheckAndShowRateApp() {
//    val showDialog = remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        val count = getAppLaunchCount()
//        val key = "rate_app"
//        if (count % 10 == 0 && !isSurveyHidden(key)) {
//            showDialog.value = true
//        }
//    }
//
//    if (showDialog.value) {
//        RateAppDialog(onDismiss = { showDialog.value = false })
//    }
//}

fun markSessionToday() {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    val raw = getValueInStorage("sessions") ?: ""
    val updated = (raw.split(",").filter { it.isNotBlank() } + today).distinct().takeLast(30)
    addValueInStorage("sessions", updated.joinToString(","))
}

fun getSessionCountLast7Days(): Int {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val sessions = getValueInStorage("sessions")?.split(",") ?: return 0

    return sessions.count { sessionDateStr ->
        runCatching {
            LocalDate.parse(sessionDateStr)
        }.getOrNull()?.let { sessionDate ->
            val daysDiff = now.daysUntil(sessionDate)
            daysDiff in -6..0 // включаем сегодня и последние 6 дней
        } ?: false
    }
}

fun shouldShowRateDialog(): Boolean {
    val now = Clock.System.now().toEpochMilliseconds()
    val installDate = getFirstLaunchDate() ?: return false
    val daysSinceInstall = (now - installDate) / (1000 * 60 * 60 * 24)

    val lastShown = getLastRateShown()
    val lastPostpone = getRatePostpone()

    val daysSinceLast = lastShown?.let { (now - it) / (1000 * 60 * 60 * 24) }
    val daysSincePostpone = lastPostpone?.let { (now - it) / (1000 * 60 * 60 * 24) }

    println("now $now")
    println("installDate $installDate")
    println("daysSinceInstall $daysSinceInstall")
    println("lastShown $lastShown")
    println("lastPostpone $lastPostpone")
    println("daysSinceLast $daysSinceLast")
    println(" >= 90 ${isAppRated() && (daysSinceLast ?: -1) >= 90}")


    println("daysSincePostpone $daysSincePostpone")
    val notRated = !isAppRated()

    return when {
        notRated && daysSinceInstall in 5..7 -> true
        notRated && (daysSincePostpone ?: -1) >= 14 -> true
        notRated && daysSinceInstall >= 30 &&
                (daysSinceLast ?: -1) > 20 &&
                (daysSincePostpone ?: -1) >= 14 -> true
        isAppRated() && (daysSinceLast ?: -1) >= 90 -> true
        else -> false
    }
}

fun shouldShowSurvey(): Boolean {
    val now = Clock.System.now().toEpochMilliseconds()
    val lastSurvey = getLastSurveyShown()
    val lastPostpone = getSurveyPostpone()

    val daysSince = lastSurvey?.let { (now - it) / (1000 * 60 * 60 * 24) } ?: Long.MAX_VALUE
    val daysSincePostpone = lastPostpone?.let { (now - it) / (1000 * 60 * 60 * 24) } ?: Long.MAX_VALUE

    val active = getSessionCountLast7Days() >= 3

    println("daysSince Survey $daysSince")
    println("daysSincePostpone Survey $daysSincePostpone")
    println("lastPostpone Survey $lastPostpone")
    println("active Survey $active")
    println("daysSince >= 30 Survey ${daysSince >= 30}")
    println("daysSincePostpone >= 14 Survey ${daysSincePostpone >= 14}")

    return active && daysSince >= 30 && daysSincePostpone >= 14
}


@Composable
fun CheckAndShowDialogs() {
    val showSurveyDialog = remember { mutableStateOf(false) }
    val showRateDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        setFirstLaunchDateIfNotSet()
        markSessionToday()

        if (shouldShowRateDialog()) showRateDialog.value = true
        if (shouldShowSurvey()) showSurveyDialog.value = true
    }

    if (showRateDialog.value) {
        RateAppDialog {
            saveLastRateShown(Clock.System.now().toEpochMilliseconds())
            showRateDialog.value = false
        }
    }

    if (showSurveyDialog.value) {
        val survey = remember { mutableStateOf<AppSurveyDto?>(null) }

        LaunchedEffect(Unit) {
            survey.value = getSurvey()
        }

        survey.value?.let {
            SurveyDialog(it) {
                saveLastSurveyShown(Clock.System.now().toEpochMilliseconds())
                showSurveyDialog.value = false
            }
        }
    }
}
