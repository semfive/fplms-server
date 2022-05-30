using System.Data;
using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/questions")]
    [TypeFilter(typeof(AuthorizationFilterAttribute))]
    public class QuestionsController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public QuestionsController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        [HttpGet]
        public async Task<IActionResult> GetAllQuestions([FromQuery] QuestionsQueryStringParameters queryStringParameters)
        {
            try
            {
                var questions = await _repositoryWrapper.QuestionRepository.GetAllQuestionsAsync(queryStringParameters);
                var metadata = new
                {
                    questions.TotalCount,
                    questions.PageSize,
                    questions.CurrentPage,
                    questions.HasPrevious,
                    questions.HasNext
                };

                Response.Headers.Add("X-Pagination", JsonConvert.SerializeObject(metadata));
                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{questionId}/answers")]
        public async Task<IActionResult> GetQuestionAnswers(Guid questionId)
        {
            try
            {
                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(questionId);
                var result = _mapper.Map<GetQuestionDto>(question);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateQuestion([FromBody] CreateQuestionDto createQuestionDto)
        {
            try
            {
                // var userEmail = HttpContext.Items["userEmail"];
                // var userRole = HttpContext.Items["userRole"];

                // if (!userRole.Equals("Student"))
                // {
                //     return Unauthorized("Only student can create questions.");
                // }

                Question question = _mapper.Map<Question>(createQuestionDto);
                var subject = await _repositoryWrapper.SubjectRepository.GetSubjectByNameAsync(createQuestionDto.SubjectName);
                question.Id = Guid.NewGuid();
                question.SubjectId = subject.Id;

                _repositoryWrapper.QuestionRepository.CreateQuestion(question);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPut("{questionId}")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> UpdateQuestion(Guid questionId, [FromBody] UpdateQuestionDto updateQuestionDto)
        {
            try
            {

                // var userEmail = HttpContext.Items["userEmail"] as string;
                // var userRole = HttpContext.Items["userRole"] as string;

                // if (!userRole.Equals("Student"))
                // {
                //     return Unauthorized("Only student can create questions.");
                // }

                // var student = await _repositoryWrapper.StudentRepository.GetStudentIdByEmail(userEmail);
                var newQuestion = _mapper.Map<Question>(updateQuestionDto);

                // if (!student.Id.Equals(newQuestion.StudentId))
                // {
                //     return Unauthorized("Only the author of the question can update the question");
                // }

                var subject = await _repositoryWrapper.SubjectRepository.GetSubjectByNameAsync(updateQuestionDto.SubjectName);
                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(questionId);

                if (question == null)
                {
                    return NotFound();
                }

                question.Title = newQuestion.Title;
                question.Content = newQuestion.Content;
                question.ModifiedDate = DateTime.Now;
                question.SubjectId = subject.Id;
                _repositoryWrapper.QuestionRepository.UpdateQuestion(question);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpDelete("{questionId}")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> DeleteQuestion([FromRoute] Guid questionId)
        {
            try
            {
                // var userEmail = HttpContext.Items["userEmail"] as string;
                // var userRole = HttpContext.Items["userRole"] as string;

                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(questionId);

                // var student = await _repositoryWrapper.StudentRepository.GetStudentIdByEmail(userEmail);
                // if (!question.StudentId.Equals(student.Id))
                // {
                //     return Unauthorized("Only the author of the question can delete the question");
                // }

                if (question == null)
                {
                    return NotFound();
                }

                // _repositoryWrapper.QuestionRepository.DeleteQuestion(question);
                question.Removed = true;
                question.RemovedBy = "N/a";
                _repositoryWrapper.QuestionRepository.UpdateQuestion(question);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}