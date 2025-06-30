package com.example.training_center.service;

import com.example.training_center.dto.QuestionRequest;
import com.example.training_center.dto.SubmitExamRequest;
import com.example.training_center.model.Exam;
import com.example.training_center.model.Question;
import com.example.training_center.model.StudentExam;
import com.example.training_center.repository.ExamRepository;
import com.example.training_center.repository.QuestionRepository;
import com.example.training_center.repository.StudentExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepo;
    private final QuestionRepository questionRepo;
    private final StudentExamRepository studentExamRepo;

    public Exam createExam(String title, List<QuestionRequest> questions) {
        // Step 1: Build and save the exam
        Exam exam = Exam.builder()
                .title(title)
                .date(LocalDate.now())
                .build();

        Exam savedExam = examRepo.save(exam); // make final reference

        // Step 2: Map all question requests to Question entities
        List<Question> qList = questions.stream().map(req ->
                Question.builder()
                        .questionText(req.getQuestionText())
                        .optionA(req.getOptionA())
                        .optionB(req.getOptionB())
                        .optionC(req.getOptionC())
                        .optionD(req.getOptionD())
                        .correctOption(req.getCorrectOption())
                        .exam(savedExam) // use the final variable inside lambda
                        .build()
        ).toList();

        questionRepo.saveAll(qList);
        savedExam.setQuestions(qList);

        return savedExam;
    }

    public int submitExam(String email, SubmitExamRequest request) {
        List<Question> questions = questionRepo.findByExamId(request.getExamId());

        int score = 0;
        for (Question q : questions) {
            String selected = request.getAnswers().get(q.getId());
            if (q.getCorrectOption().equalsIgnoreCase(selected)) {
                score++;
            }
        }

        StudentExam studentExam = StudentExam.builder()
                .studentEmail(email)
                .examId(request.getExamId())
                .score(score)
                .submittedAt(LocalDateTime.now())
                .build();

        studentExamRepo.save(studentExam);
        return score;
    }
    public List<Exam> getAllExams() {
        return examRepo.findAll();
    }

    public List<Question> getQuestionsByExamId(Long examId) {
        return questionRepo.findByExamId(examId);
    }

}
