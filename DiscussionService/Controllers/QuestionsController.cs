using System.Data;
using AutoMapper;
using DiscussionService.ActionFilters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/questions")]
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
        public async Task<IActionResult> GetAllQuestions()
        {
            try
            {
                var questions = await _repositoryWrapper.QuestionRepository.GetAllQuestionsAsync();
                var result = _mapper.Map<List<GetQuestionDto>>(questions);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{questionId}")]
        public async Task<IActionResult> GetQuestionById(Guid questionId)
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
                Question question = _mapper.Map<Question>(createQuestionDto);
                question.Id = Guid.NewGuid();
                question.CreatedDate = DateTime.Now;

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

        [HttpDelete]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> DeleteQuestion(Guid questionId)
        {
            try
            {
                _repositoryWrapper.QuestionRepository.DeleteQuestion(new Question { Id = questionId });
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